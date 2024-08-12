package com.hivemc.chunker.mapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueBoolean;
import com.hivemc.chunker.mapping.identifier.states.StateValueInt;
import com.hivemc.chunker.mapping.identifier.states.StateValueString;
import com.hivemc.chunker.mapping.mappings.IdentifierMappings;
import com.hivemc.chunker.mapping.mappings.TypeMapping;
import com.hivemc.chunker.mapping.mappings.TypeMappings;
import com.hivemc.chunker.nbt.tags.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests which can be performed on blocks and items mappings, that ensure the parsed result is expected.
 */
public abstract class MappingFileTests {
    public static File tempFile() throws IOException {
        File tempFile = File.createTempFile("mappings", ".json");
        tempFile.deleteOnExit();
        return tempFile;
    }

    public abstract Optional<Identifier> convert(MappingsFile mappingsFile, Identifier input);

    public abstract MappingsFile loadMappings(String input);

    public abstract MappingsFile loadMappingsFile(String input) throws IOException;

    public abstract Map<String, IdentifierMappings> getLookup(MappingsFile mappingsFile);

    @Test
    public void testBasicIdentifier() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone"
                            }
                          ]
                        }
                """);
        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Collections.emptyMap()),
                new Identifier("minecraft:stone", Collections.emptyMap())
        );
    }

    @Test
    public void testBasicIdentifier2() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "granite"
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:stone", Map.of("stone_type", new StateValueString("granite")))
        );
    }

    @Test
    public void testBasicIdentifier3() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": 1
                              },
                              "new_state_values": {
                                "stone_type": 2
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueInt(1))),
                new Identifier("minecraft:stone", Map.of("stone_type", new StateValueInt(2)))
        );
    }

    @Test
    public void testBasicIdentifier4() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": true
                              },
                              "new_state_values": {
                                "stone_type": false
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", StateValueBoolean.TRUE)),
                new Identifier("minecraft:stone", Map.of("stone_type", StateValueBoolean.FALSE))
        );
    }

    @Test
    public void testBasicIdentifierMultipleSameStateCount() {
        // This test ensures that lookups still work with the same number of keys but different names
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": true
                              },
                              "new_state_values": {
                                "stone_type": false
                              }
                            },
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "hello": "world"
                              },
                              "new_state_values": {
                                "stone_type": true
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", StateValueBoolean.TRUE)),
                new Identifier("minecraft:stone", Map.of("stone_type", StateValueBoolean.FALSE))
        );
        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("hello", new StateValueString("world"))),
                new Identifier("minecraft:stone", Map.of("stone_type", StateValueBoolean.TRUE))
        );
    }

    @Test
    public void testBasicIdentifierInconsistentStateOrder() {
        // Order shouldn't matter and the lookup should still work
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": true,
                                "hello": "world"
                              },
                              "new_state_values": {
                                "stone_type": false
                              }
                            },
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "hello": "world",
                                "color": false
                              },
                              "new_state_values": {
                                "stone_type": true
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", StateValueBoolean.TRUE, "hello", new StateValueString("world"))),
                new Identifier("minecraft:stone", Map.of("stone_type", StateValueBoolean.FALSE))
        );
        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("hello", new StateValueString("world"), "color", StateValueBoolean.FALSE)),
                new Identifier("minecraft:stone", Map.of("stone_type", StateValueBoolean.TRUE))
        );
    }

    @Test
    public void testBasicIdentifierPreferMoreMatches() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": true
                              },
                              "new_state_values": {
                                "stone_type": false
                              }
                            },
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": true,
                                "hello": "world"
                              },
                              "new_state_values": {
                                "stone_type": true
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", StateValueBoolean.TRUE, "hello", new StateValueString("world"))),
                new Identifier("minecraft:stone", Map.of("stone_type", StateValueBoolean.TRUE))
        );
    }

    @Test
    public void testBasicIdentifierNoMatch() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "granite"
                              }
                            }
                          ]
                        }
                """);

        assertEmptyMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("blue")))
        );
    }

    @Test
    public void testBasicIdentifierNoMatch2() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color2": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "granite"
                              }
                            }
                          ]
                        }
                """);

        assertEmptyMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("blue")))
        );
    }

    @Test
    public void testBasicIdentifierRedundant() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "granite"
                              }
                            },
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "diorite"
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:stone", Map.of("stone_type", new StateValueString("granite")))
        );
    }

    @Test
    public void testBasicIdentifierRedundantInverse() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "granite"
                              }
                            },
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "diorite"
                              }
                            }
                          ]
                        }
                """).inverse();

        // It is important this isn't reversible as it means the inverse() order is not correct
        assertNotReversibleMapping(
                mappingsFile,
                new Identifier("minecraft:stone", Map.of("stone_type", new StateValueString("diorite"))),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange")))
        );
    }

    @Test
    public void testBasicIdentifierRetainsVirtual() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "granite"
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"), "virtual:test", new StateValueString("Test"))),
                new Identifier("minecraft:stone", Map.of("stone_type", new StateValueString("granite"), "virtual:test", new StateValueString("Test")))
        );
    }

    @Test
    public void testBasicIdentifierRetainsMeta() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": "orange"
                              },
                              "new_state_values": {
                                "stone_type": "granite"
                              }
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"), "meta:test", new StateValueString("Test"))),
                new Identifier("minecraft:stone", Map.of("stone_type", new StateValueString("granite"), "meta:test", new StateValueString("Test")))
        );
    }

    @Test
    public void testNotFoundBlockIdentifier() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": []
                        }
                """);

        assertEmptyMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Collections.emptyMap())
        );
    }

    @Test
    public void testNotFoundBlockIdentifier2() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": []
                        }
                """);

        assertEmptyMapping(
                mappingsFile,
                new Identifier("mymod:wool", Collections.emptyMap())
        );
    }

    @Test
    public void testIdentifierStateListWildcard() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "*"
                            }
                          ]
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange")))
        );
    }

    @Test
    public void testIdentifierStateListNoWildcard() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool"
                            }
                          ]
                        }
                """);

        assertSameMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool")
        );
    }

    @Test
    public void testIdentifierStateListRename() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color2"
                            }]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool", Map.of("color2", new StateValueString("orange")))
        );
    }

    @Test
    public void testIdentifierStateListRename2() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color2",
                                "type": ""
                            }]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool", Map.of("color2", new StateValueString("orange")))
        );
    }

    @Test
    public void testIdentifierStateListNotFound() {
        assertThrowsExactly(JsonParseException.class, () -> loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ]
                        }
                """));
    }

    @Test
    public void testIdentifierStateListTypes() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                }
                            ]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("red")))
        );
    }

    @Test
    public void testIdentifierStateListTypesRedundant() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                },
                                {
                                    "input": "orange",
                                    "output": "blue"
                                }
                            ]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("red")))
        );
    }

    @Test
    public void testIdentifierStateListTypesRedundantEquality() {
        String input = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                },
                                {
                                    "input": "orange",
                                    "output": "blue"
                                }
                            ],
                            "color_changer2": [
                                {
                                    "input": ["orange", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                }
                            ]
                          }
                        }
                """;

        MappingsFile mappingsFile = loadMappings(input);
        MappingsFile mappingsFile2 = loadMappings(input);
        assertEquals(mappingsFile, mappingsFile2);
    }

    @Test
    public void testIdentifierStateListTypesRedundantToString() {
        String input = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                },
                                {
                                    "input": "orange",
                                    "output": "blue"
                                }
                            ],
                            "color_changer2": [
                                {
                                    "input": ["orange", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                }
                            ]
                          }
                        }
                """;

        MappingsFile mappingsFile = loadMappings(input);
        MappingsFile mappingsFile2 = loadMappings(input);
        assertEquals(mappingsFile.toString(), mappingsFile2.toString());
    }

    @Test
    public void testIdentifierStateListTypesRedundantHash() {
        String input = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                },
                                {
                                    "input": "orange",
                                    "output": "blue"
                                }
                            ],
                            "color_changer2": [
                                {
                                    "input": ["orange", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                }
                            ]
                          }
                        }
                """;

        MappingsFile mappingsFile = loadMappings(input);
        MappingsFile mappingsFile2 = loadMappings(input);
        assertEquals(mappingsFile.hashCode(), mappingsFile2.hashCode());
    }

    @Test
    public void testIdentifierStateListTypesRedundantDoubleInverseEquality() {
        String input = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                },
                                {
                                    "input": "orange",
                                    "output": "blue"
                                }
                            ],
                            "color_changer2": [
                                {
                                    "input": ["orange", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                },
                                {
                                    "input": ["green", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["blue", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                }
                            ],
                            "color_changer3": [
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["green", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["blue", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                }
                            ]
                          }
                        }
                """;

        MappingsFile mappingsFile = loadMappings(input);
        assertEquals(mappingsFile, mappingsFile.inverse().inverse());
    }

    @Test
    public void testIdentifierStateListTypesRedundantJSONEquality() {
        String input = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            },
                            {
                              "old_identifier": "mymod:test",
                              "new_identifier": "mymod:test2",
                              "old_state_values": {
                                "test": 1
                              },
                              "new_state_values": {
                                "hi": "hello"
                              }
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }],
                            "multiple": [{
                                "old_state": ["shade", "color"],
                                "new_state": ["shade", "color"]
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                },
                                {
                                    "input": "orange",
                                    "output": "blue"
                                }
                            ],
                            "color_changer2": [
                                {
                                    "input": ["orange", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                },
                                {
                                    "input": ["green", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["blue", true],
                                    "output": "red"
                                }
                            ]
                          }
                        }
                """;

        MappingsFile mappingsFile = loadMappings(input);

        // Serialize then load
        JsonElement serialized = mappingsFile.toJson();
        MappingsFile mappingsFile2 = MappingsFile.load(serialized);

        // Check for equality
        assertEquals(mappingsFile, mappingsFile2);
    }

    @Test
    public void testIdentifierStateListTypesRedundantJSONEquality2() {
        String input = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            },
                            {
                              "old_identifier": "mymod:test",
                              "new_identifier": "mymod:test2",
                              "old_state_values": {
                                "test": 1
                              },
                              "new_state_values": {
                                "hi": "hello"
                              }
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }],
                            "multiple": [{
                                "old_state": ["shade", "color"],
                                "new_state": ["shade", "color"]
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                },
                                {
                                    "input": "orange",
                                    "output": "blue"
                                }
                            ],
                            "color_changer2": [
                                {
                                    "input": ["orange", true],
                                    "output": "red"
                                },
                                {
                                    "input": ["orange", true],
                                    "output": "blue"
                                }
                            ]
                          }
                        }
                """;

        MappingsFile mappingsFile = loadMappings(input);

        // Serialize then load
        String serialized = mappingsFile.toJsonString();
        MappingsFile mappingsFile2 = MappingsFile.load(serialized);

        // Check for equality
        assertEquals(mappingsFile, mappingsFile2);
    }

    @Test
    public void testIdentifierStateListCustomBlock() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "$custom_block",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                }
                            ]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("mymod:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("mymod:wool", Map.of("color", new StateValueString("red")))
        );
    }

    @Test
    public void testIdentifierStateListCustomBlock2() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "$custom_block",
                              "new_identifier": "$custom_block",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                }
                            ]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("mymod:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("mymod:wool", Map.of("color", new StateValueString("red")))
        );
    }

    @Test
    public void testIdentifierStateListMultiTypes() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": ["color", "test"],
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": ["red", true]
                                }
                            ]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("red"), "test", StateValueBoolean.TRUE))
        );
    }

    @Test
    public void testIdentifierStateListTypesNotFound() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                }
                            ]
                          }
                        }
                """);

        assertSameMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("green"))),
                new Identifier("minecraft:wool", Map.of())
        );
    }

    @Test
    public void testIdentifierStateListTypesNotFound2() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color2",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                }
                            ]
                          }
                        }
                """);

        assertSameMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("green"))),
                new Identifier("minecraft:wool", Map.of())
        );
    }

    @Test
    public void testIdentifierStateListTypesNotFoundDefault() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [
                                {
                                    "old_state": "color",
                                    "new_state": "color",
                                    "type": "color_changer"
                                },
                                {
                                    "old_state": "",
                                    "new_state": "color",
                                    "type": "default_color"
                                },
                                {
                                    "old_state": "color",
                                    "new_state": "",
                                    "type": "default_color"
                                }
                            ]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                }
                            ],
                            "default_color": [
                                {
                                    "input": "",
                                    "output": "blue"
                                },
                                {
                                    "input": "blue",
                                    "output": ""
                                }
                            ]
                          }
                        }
                """);

        assertSameMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of()),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("blue")))
        );
    }

    @Test
    public void testIdentifierStateListTypesNotFoundDefault2() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [
                                {
                                    "old_state": "color",
                                    "new_state": "color",
                                    "type": "color_changer"
                                },
                                {
                                    "old_state": "",
                                    "new_state": "color",
                                    "type": "default_color"
                                },
                                {
                                    "old_state": "color",
                                    "new_state": "",
                                    "type": "default_color"
                                }
                            ]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "green"
                                }
                            ],
                            "default_color": [
                                {
                                    "input": "",
                                    "output": "blue"
                                },
                                {
                                    "input": "blue",
                                    "output": ""
                                }
                            ]
                          }
                        }
                """);

        assertSameMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("red"))),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("blue")))
        );
    }

    @Test
    public void testIdentifierStateListTypesNotFoundDefaultOther() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [
                                {
                                    "old_state": "color",
                                    "new_state": "color",
                                    "type": "color_changer"
                                },
                                {
                                    "old_state": "",
                                    "new_state": "color",
                                    "type": "default_color"
                                }
                            ]
                          },
                          "types": {
                            "color_changer": [
                                {
                                    "input": "orange",
                                    "output": "red"
                                }
                            ],
                            "default_color": [
                                {
                                    "input": "",
                                    "output": "blue"
                                }
                            ]
                          }
                        }
                """);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("orange"))),
                new Identifier("minecraft:wool", Map.of("color", new StateValueString("red")))
        );
    }

    @Test
    public void testIdentifierStateListTypeNotFound() {
        assertThrowsExactly(JsonParseException.class, () -> loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:wool",
                              "state_list": "wool"
                            }
                          ],
                          "state_lists": {
                            "wool": [{
                                "old_state": "color",
                                "new_state": "color",
                                "type": "color_changer"
                            }]
                          }
                        }
                """));
    }

    @Test
    public void testReadingInvalidStateValue() {
        assertThrowsExactly(JsonParseException.class, () -> loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "test": [1, 2, 3]
                              }
                            }
                          ]
                        }
                """));
    }

    @Test
    public void testWritingInvalidStateValue() {
        MappingsFile mappingsFile = loadMappings("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone"
                            }
                          ],
                          "types": {}
                        }
                """);

        // Invalid list
        List<StateValue<?>> list = new ArrayList<>();
        list.add(new StateValue<>() {
            @Override
            public Object getBoxed() {
                return null;
            }

            @Override
            public Tag<?> toNBT() {
                return null;
            }
        });

        mappingsFile.getTypeMappings().put("invalid", new TypeMappings(
                "test",
                Map.of(list, new TypeMapping(0, list, list)),
                Collections.emptyList()
        ));
        assertThrowsExactly(IllegalArgumentException.class, mappingsFile::toJson);
    }

    @Test
    public void testIdentifierName() {
        String contents = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone"
                            }
                          ]
                        }
                """;
        MappingsFile mappingsFile = loadMappings(contents);
        assertEquals("minecraft:wool", getLookup(mappingsFile).get("minecraft:wool").getIdentifier());
    }

    @Test
    public void testStateListEmptyName() {
        String contents = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "state_list": ""
                            }
                          ]
                        }
                """;
        MappingsFile mappingsFile = loadMappings(contents);
        assertEquals(0, getLookup(mappingsFile).get("minecraft:wool").getLookup().get(Collections.emptySet()).get(Collections.emptyList()).getStateMapping().getMappings().size());
    }

    @Test
    public void testTypeName() {
        String contents = """
                        {
                          "state_lists": {
                            "test": [
                                {
                                    "old_state": "hello",
                                    "new_state": "world",
                                    "type": "test"
                                }
                            ]
                          },
                          "types": {
                            "test": []
                          }
                        }
                """;
        MappingsFile mappingsFile = loadMappings(contents);
        assertEquals("test", mappingsFile.getStateMappings().get("test").getMappings().get(0).getTypeMapping().getName());
    }

    @Test
    public void testTypeRedundantSize() {
        String contents = """
                        {
                          "types": {
                            "test": [
                                {
                                    "input": 1,
                                    "output": 2
                                },
                                {
                                    "input": 1,
                                    "output": 2
                                }
                            ]
                          }
                        }
                """;
        MappingsFile mappingsFile = loadMappings(contents);
        assertEquals(1, mappingsFile.getTypeMappings().get("test").getRedundant().size());
    }

    @Test
    public void testFileLoading() throws IOException {
        String contents = """
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone",
                              "old_state_values": {
                                "color": 1
                              },
                              "new_state_values": {
                                "stone_type": 2
                              }
                            }
                          ]
                        }
                """;
        MappingsFile mappingsFile = loadMappings(contents);
        MappingsFile mappingsFile2 = loadMappingsFile(contents);

        assertMapping(
                mappingsFile,
                new Identifier("minecraft:wool", Map.of("color", new StateValueInt(1))),
                new Identifier("minecraft:stone", Map.of("stone_type", new StateValueInt(2)))
        );
        assertEquals(mappingsFile, mappingsFile2);
    }

    public void assertMapping(MappingsFile mappingsFile, Identifier input, Identifier expectedOutput) {
        assertEquals(Optional.ofNullable(expectedOutput), convert(mappingsFile, input));

        // All mappings are assumed to be the same in reverse
        MappingsFile inverse = mappingsFile.inverse();
        assertEquals(Optional.ofNullable(input), convert(inverse, expectedOutput));
    }

    public void assertNotReversibleMapping(MappingsFile mappingsFile, Identifier input, Identifier expectedOutput) {
        assertEquals(Optional.ofNullable(expectedOutput), convert(mappingsFile, input));

        // All mappings are assumed to be the same in reverse
        MappingsFile inverse = mappingsFile.inverse();
        assertNotEquals(Optional.ofNullable(input), convert(inverse, expectedOutput));
    }

    public void assertEmptyMapping(MappingsFile mappingsFile, Identifier input) {
        assertSameMapping(mappingsFile, input, null);
    }

    public void assertSameMapping(MappingsFile mappingsFile, Identifier input, Identifier expectedOutput) {
        assertEquals(Optional.ofNullable(expectedOutput), convert(mappingsFile, input));

        // All mappings are assumed to be the same in reverse
        MappingsFile inverse = mappingsFile.inverse();
        assertEquals(Optional.ofNullable(expectedOutput), convert(inverse, input));
    }
}
