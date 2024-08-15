import json
import os
import platform
import requests
import shutil
import subprocess
import zipfile

# Determine the correct classpath separator
if platform.system() == 'Windows':
    classpath_separator = ';'
else:
    classpath_separator = ':'


def download_version(version_id, version_url):
    if not os.path.isdir("java-temp"):
        os.makedirs("java-temp")
    download_directory = os.path.join("java-temp", f"server-{version_id}")

    # Download the server.jar if it hasn't been downloaded
    server_jar = os.path.join(download_directory, "server.jar")
    server_jar_extracted = os.path.join(download_directory, "server_extracted.jar")
    libraries_folder = os.path.join(download_directory, "libraries")
    if not os.path.isfile(server_jar) and not os.path.isfile(server_jar_extracted):
        print(f"Downloading Java {version_id}")
        if not os.path.isdir(download_directory):
            os.makedirs(download_directory)

        # Create the request to the version json
        response = requests.get(version_url)
        version_data = response.json()
        download_url = version_data["downloads"]["server"]["url"]

        # Download the server.jar
        server_jar_request = requests.get(download_url)
        if server_jar_request.status_code != 200:
            raise Exception(f"Failed to fetch {version_id}")
        with open(server_jar, "wb") as file:
            file.write(server_jar_request.content)
        print(version_data)

    # Get the required libraries / files out the jar so we can run the reporting tool
    if not os.path.isfile(server_jar_extracted):
        print(f"Extracting Java {version_id}")
        # Ensure the libraries folder is present
        if not os.path.isdir(libraries_folder):
            os.makedirs(libraries_folder)

        # Extract the server & libraries
        with zipfile.ZipFile(server_jar, "r") as zip:
            # Extract the version data
            if "version.json" in zip.namelist():
                zip.extract("version.json", download_directory)

            # Extract the server jar
            raw_jar = [f for f in zip.namelist() if f.startswith("META-INF/versions/") and f.endswith(".jar")]
            if len(raw_jar) > 0:
                # Rename file to ensure it gets extracted to our target name
                zip.getinfo(raw_jar[0]).filename = os.path.basename(server_jar_extracted)
                zip.extract(raw_jar[0], download_directory)

                # Extract all the libraries
                libraries = [f for f in zip.namelist() if f.startswith("META-INF/libraries") and f.endswith(".jar")]
                for library in libraries:
                    # Rename file to ensure it gets extracted to our target name
                    zip.getinfo(library).filename = os.path.basename(library)
                    zip.extract(library, libraries_folder)
            else:
                # Close the zip as it's not needed
                zip.close()

                # Jar is not nested
                os.rename(server_jar, server_jar_extracted)

    reports_folder = os.path.join(download_directory, "reports")

    # Generate the reports for this version
    if not os.path.isdir(reports_folder):
        print(f"Fetching reports for Java {version_id}")
        try:
            process = subprocess.Popen(
                [
                    "java",
                    "-cp",
                    f"libraries/*{classpath_separator}server_extracted.jar",
                    "net.minecraft.data.Main",
                    "--all",
                    "--output",
                    "reports"
                ],
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE, universal_newlines=True, cwd=download_directory)

            # Read the console lines to ensure that this went successfully
            try:
                for line in iter(process.stdout.readline, ""):
                    print(line.strip())
            finally:
                process.stdout.close()
                process.wait()

            # Print STDERR
            for line in iter(process.stderr.readline, ""):
                print(line)
        except OSError as e:
            print(e)

    data_folder = os.path.join("java", version_id)
    if os.path.isdir(reports_folder) and not os.path.isdir(data_folder):
        print(f"Copying useful data from Java {version_id}")
        os.makedirs(data_folder)

        # Copy version.json if it's present
        version_data_file = os.path.join(download_directory, "version.json")
        if os.path.exists(version_data_file):
            shutil.copy(version_data_file, os.path.join(data_folder, "version.json"))

        # Copy files from reports
        actual_reports_folder = os.path.join(reports_folder, "reports")

        # Copy blocks.json
        shutil.copy(os.path.join(actual_reports_folder, "blocks.json"), os.path.join(data_folder, "blocks.json"))

        # Copy the items if it's the items.json, otherwise it'll need to be extracted from registries.json
        items_output = os.path.join(data_folder, "items.json")
        possible_items = os.path.join(actual_reports_folder, "items.json")
        if os.path.exists(possible_items):
            shutil.copy(possible_items, items_output)
        else:
            # Parse the JSON of registries.json
            with open(os.path.join(actual_reports_folder, "registries.json"), "r") as file:
                registries_json = json.load(file)

                # Save minecraft:item
                obj = registries_json["minecraft:item"]["entries"]
                with open(items_output, "w") as file:
                    json.dump(obj, file, indent=2)


# Fetch the version list and download the versions down to 1.8.7 marked as release
response = requests.get("https://launchermeta.mojang.com/mc/game/version_manifest.json")
version_manifest = response.json()

for version in version_manifest["versions"]:
    if version["type"] == "snapshot":
        continue
    if version["id"] == "1.8.7":
        break  # Don't download versions below 1.8.8
    download_version(version["id"], version["url"])
