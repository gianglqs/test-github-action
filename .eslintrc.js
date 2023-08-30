/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

module.exports = {
    env: {
      browser: true,
      es2021: true,
      node: true,
      jest: true
    },
    extends: [
      'eslint:recommended',
      'plugin:@typescript-eslint/recommended',
      'plugin:react/recommended',
      'plugin:prettier/recommended'
    ],
    parser: '@typescript-eslint/parser',
    parserOptions: {
      ecmaFeatures: {
        jsx: true
      },
      ecmaVersion: 12,
      sourceType: 'module'
    },
    plugins: ['react', '@typescript-eslint', 'prettier'],
    rules: {
      'prettier/prettier': [
        'error',
        {
          singleQuote: true,
          semi: false,
          trailingComma: 'none',
          'space-before-function-paren': ['error'],
          printWidth: 120
        }
      ],
      'react/react-in-jsx-scope': 'off',
      'jsx-quotes': ['error', 'prefer-double'],
      'react/jsx-curly-newline': 'off',
      'react/prop-types': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      'react/display-name': 'off',
      'react/jsx-handler-names': 'off',
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          ignoreRestSiblings: true
        }
      ],
      'no-console': ['error', { allow: ['warn', 'error'] }],
      'no-duplicate-imports': 'error',
      'no-empty-function': 'error',
      'no-unused-vars': 'error'
      // 'no-use-before-define': 'off',
      // 'no-unused-expressions': 'off',
      // 'no-useless-escape': 'off',
      // 'generator-star-spacing': ['error', {
      //   before: false, after: true
      // }],
      // 'yield-star-spacing': ['error', 'after'],
      // camelcase: 'off',
      // 'react/jsx-key': 'off',
      // 'no-useless-return': 'off'
    }
  }
  