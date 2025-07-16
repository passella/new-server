package br.com.passella.jsonparser.exceptions

class NullValueException(key: String) : RuntimeException("Value is null: $key")