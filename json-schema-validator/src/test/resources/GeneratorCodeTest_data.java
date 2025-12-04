JsonObject.optional(
        JsonBoolean.optional("b",null).setExampleValue(true),
        JsonBoolean.optional("a",null).setExampleValue(false),
        JsonArray.optional("bools",null,
            JsonBoolean.make().setExampleValue(true)
        )
);