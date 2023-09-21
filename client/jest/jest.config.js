module.exports = {
    verbose: true,
    testRegex: "\\.test\\.tsx?$",
    moduleFileExtensions: ["js", "ts", "tsx"],
    testEnvironment: "jsdom",
    transform: {
        "^.+\\.(tsx|ts)?$": "ts-jest",
        "^.+\\.css$": "./dummyTransformer",
    },
    transformIgnorePatterns: ["/node_modules/", "\\.svg$"],
    moduleNameMapper: {
        "\\.svg$": "identity-obj-proxy",
    },
    setupFilesAfterEnv: ["@testing-library/jest-dom/extend-expect"],
    roots: ["../src"]
};