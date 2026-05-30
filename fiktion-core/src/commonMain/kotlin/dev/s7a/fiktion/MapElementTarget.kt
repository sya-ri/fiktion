package dev.s7a.fiktion

/**
 * Targets keys generated for the current map root.
 */
public val <Key, Value, MapType : Map<Key, Value>> FakeSpec<MapType>.key: RuleTarget<Key>
    get() = mapKeyTarget(mapType = rootType)

/**
 * Targets values generated for the current map root.
 */
public val <Key, Value, MapType : Map<Key, Value>> FakeSpec<MapType>.value: RuleTarget<Value>
    get() = mapValueTarget(mapType = rootType)

/**
 * Targets keys generated for the map selected by this target.
 */
public val <Key, Value, MapType : Map<Key, Value>> RuleTarget<MapType>.key: RuleTarget<Key>
    get() =
        (this as DefaultRuleTarget<*>).containerPartTarget(
            kind = ContainerPart.Kind.MapKey,
            resultType = { mapType -> mapType.typeArgument(index = 0) },
        )

/**
 * Targets values generated for the map selected by this target.
 */
public val <Key, Value, MapType : Map<Key, Value>> RuleTarget<MapType>.value: RuleTarget<Value>
    get() =
        (this as DefaultRuleTarget<*>).containerPartTarget(
            kind = ContainerPart.Kind.MapValue,
            resultType = { mapType -> mapType.typeArgument(index = 1) },
        )
