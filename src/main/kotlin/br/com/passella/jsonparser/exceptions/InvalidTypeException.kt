package br.com.passella.jsonparser.exceptions

class InvalidTypeException(key: String, expectedType: String) : RuntimeException("Value is not a $expectedType: $key")