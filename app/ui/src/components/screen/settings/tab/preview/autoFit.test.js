import {computeMinZoom} from "./autoFit";

describe("computeMinZoom", () => {
    test("small world: ~200x200 chunks -> -3", () => {
        expect(computeMinZoom({minX: 0, minZ: 0, maxX: 199, maxZ: 199})).toBe(-3);
    });
    test("medium world: 1000x1000 chunks -> -5", () => {
        expect(computeMinZoom({minX: 0, minZ: 0, maxX: 999, maxZ: 999})).toBe(-5);
    });
    test("very large world: 10000x10000 chunks -> -9", () => {
        expect(computeMinZoom({minX: 0, minZ: 0, maxX: 9999, maxZ: 9999})).toBe(-9);
    });
    test("huge world: 50000x50000 chunks -> -11", () => {
        expect(computeMinZoom({minX: 0, minZ: 0, maxX: 49999, maxZ: 49999})).toBe(-11);
    });
    test("massive world capped at -12", () => {
        expect(computeMinZoom({minX: 0, minZ: 0, maxX: 200000, maxZ: 200000})).toBe(-12);
    });
    test("null bounds yields the cap", () => {
        expect(computeMinZoom(null)).toBe(-12);
    });
});
