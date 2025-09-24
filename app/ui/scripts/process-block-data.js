const fs = require('fs');
const path = require('path');
const { promisify } = require('util');
const glob = promisify(fs.glob);

// Process all the block data in the local `data` folder and output to `public/data`, this formats the java reports /
// bedrock data into a format that Chunker uses for suggestions.
async function processBlockData() {
    const inputDataPath = path.join(__dirname, 'data');
    const outputDataPath = path.join(__dirname, '../public/data');

    // Ensure we have input files
    if (!fs.existsSync(inputDataPath)) {
        console.error("Missing input block data at ", inputDataPath)
        return;
    }

    // Create the output directory if it's not found
    if (!fs.existsSync(outputDataPath)) {
        fs.mkdirSync(outputDataPath);
    }

    // Loop through each json file
    try {
        const files = await glob('**/*.json', { cwd: inputDataPath });

        const promises = files.map(async (file) => {
            const fullInputPath = path.join(inputDataPath, file);
            const fullOutputPath = path.join(outputDataPath, file);
            try {
                // Load the input
                const content = fs.readFileSync(fullInputPath, 'utf8');

                // Parse as JSON
                const parsed = JSON.parse(content);

                // Process the JSON
                const output = processBlockDataJson(parsed);

                // Convert back to JSON
                const minified = JSON.stringify(output);

                // Ensure the output directory exists
                const directory = path.dirname(fullOutputPath);
                if (!fs.existsSync(directory)) {
                    fs.mkdirSync(directory);
                }

                // Write the file
                fs.writeFileSync(fullOutputPath, minified);
            } catch (error) {
                console.error(`Error processing ${file}:`, error.message);
            }
        });

        // Wait for completion
        await Promise.all(promises);

        console.log(`Processed ${files.length} block data files`);
    } catch (error) {
        console.error('Error finding block data files:', error.message);
    }
}

function processBlockDataJson(input) {
    let blocks = [];

    // Apply pre-processing to sort it into states
    if (input instanceof Array) {
        // Legacy Java format
        for (let entry of input) {
            blocks.push({
                name: "minecraft:" + entry.name,
                states: {
                    data: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
                }
            })
        }
    } else {
        // Either Java or Bedrock format
        if (input.hasOwnProperty("blocks")) {
            // Bedrock palette
            let blockMap = {};

            // Loop through the entries and build the block map
            for (let entry of input.blocks) {
                let block = blockMap[entry.name];

                // Create a new entry
                if (!block) {
                    block = {
                        waterlogged: new Set([false, true])
                    }
                    blockMap[entry.name] = block;
                }

                // Merge any state values
                if (entry.hasOwnProperty("states")) {
                    for (let state of entry.states) {
                        let states = block[state.name];

                        // Create a new state entry
                        if (!states) {
                            states = new Set();
                            block[state.name] = states;
                        }

                        // Add the known states (special case for byte)
                        states.add(state.type === "byte" ? (state.value === 1) : state.value);
                    }
                }
            }

            // Finally convert the blocks into an array
            for (let name of Object.keys(blockMap)) {
                // Turn the states into arrays
                let states = blockMap[name];
                for (let name in states) {
                    states[name] = Array.from(states[name]);
                }

                // Add the block
                blocks.push({
                    name: name,
                    states: states
                });
            }
        } else {
            // Java blocks report
            for (let name of Object.keys(input)) {
                blocks.push({
                    name: name,
                    states: input[name].properties
                })
            }
        }
    }

    // Sort blocks by name
    blocks.sort((a, b) => a.name.localeCompare(b.name));

    // Return the processed object
    return blocks;
}

// Run processBlockData async
(async () => {
    await processBlockData();
})();