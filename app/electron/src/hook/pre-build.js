import * as git from "git-last-commit";
import fs from "fs";

git.getLastCommit(function (err, commit) {
    if (commit === undefined) return;
    let data = {
        "version": JSON.parse(fs.readFileSync("package.json")).version,
        "git": commit.branch.replace("HEAD", "master") + "-" + commit.shortHash,
        "time": new Date().toISOString()
    };

    fs.writeFileSync("src/version.json", JSON.stringify(data));
}, {
    dst: "../"
});