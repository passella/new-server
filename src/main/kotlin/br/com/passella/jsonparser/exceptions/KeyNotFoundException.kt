package br.com.passella.jsonparser.exceptions

class KeyNotFoundException(key: String) : RuntimeException("Key not found: $key")