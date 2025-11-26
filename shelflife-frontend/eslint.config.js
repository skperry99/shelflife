import js from "@eslint/js";
import globals from "globals";
import react from "eslint-plugin-react";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import { defineConfig, globalIgnores } from "eslint/config";

export default defineConfig([
  // Ignore build artifacts globally
  globalIgnores(["dist", "node_modules"]),

  // Main app: React in the browser
  {
    files: ["**/*.{js,jsx}"],
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      globals: globals.browser,
      parserOptions: {
        ecmaFeatures: { jsx: true },
      },
    },
    // IMPORTANT: we don't need a `plugins` block here, the flat configs
    // from these plugins already register themselves.
    extends: [
      js.configs.recommended,
      // React core rules
      react.configs.flat.recommended,
      // JSX runtime (no need for React in scope)
      react.configs.flat["jsx-runtime"],
      // Hooks rules (exhaustive deps, etc.)
      reactHooks.configs.flat.recommended,
      // Vite + react-refresh integration
      reactRefresh.configs.vite,
    ],
    rules: {
      // Ignore ALL_CAPS / PascalCase "unused" (often components / constants)
      "no-unused-vars": ["error", { varsIgnorePattern: "^[A-Z_]" }],
      // Add/tweak react rules here if you want later, e.g.:
      // "react/prop-types": "off",
    },
    settings: {
      react: {
        version: "detect",
      },
    },
  },

  // Node-style config files (eslint, vite, etc.)
  {
    files: [
      "eslint.config.js",
      "vite.config.*",
      "*.config.js",
      "*.config.cjs",
      "*.config.mjs",
    ],
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      globals: globals.node,
    },
    rules: {
      // Slightly softer here if you like
      "no-unused-vars": ["warn", { varsIgnorePattern: "^[A-Z_]" }],
    },
  },
]);
