package dev.s7a.fiktion

/**
 * Targets elements generated for the current collection root.
 */
public val <Element, CollectionType : Collection<Element>> FakeSpec<CollectionType>.element: RuleTarget<Element>
    get() = collectionElementTarget(collectionType = rootType)

/**
 * Targets elements generated for the collection selected by this target.
 */
public val <Element, CollectionType : Collection<Element>> RuleTarget<CollectionType>.element: RuleTarget<Element>
    get() =
        (this as DefaultRuleTarget<*>).containerPartTarget(
            kind = ContainerPart.Kind.Collection,
            resultType = { collectionType -> collectionType.typeArgument(index = 0) },
        )
