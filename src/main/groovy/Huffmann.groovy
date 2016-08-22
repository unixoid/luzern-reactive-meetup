class Item {
    List items
    int count
    String bits = ''

    @Override
    String toString() {
        "(${items?.size()} items=${items}, count=${count}, bits=${bits})"
    }
}

List<Item> tail2(List<Item> a) {
    (a.size() > 2) ? a[2..-1] : []
}

List<Item> topDown(List<Item> a) {
    topDownSorted(a.sort { it.count } )
}

List<Item> topDownSorted(List<Item> a) {
    (a.size() == 1) ?
            a :
            topDown(tail2(a) + new Item(items: [a[0], a[1]], count: a[0].count + a[1].count))
}

Set<String> stringToSet(String s) {
    new HashSet(s.collect { new String(it) } )
}

List<Item> bottomUpSingleItem(Item a) {
    (a.items[0] instanceof Item) ?
            bottomUp(new Item(items: ((Item) a.items[0]).items, bits: a.bits)) :
            [a]
}

List<Item> bottomUp(Item a) {
    (a.items.size() == 1) ?
            bottomUpSingleItem(a) :
            bottomUp(new Item(items: [a.items[0]], bits: a.bits + '0')) + bottomUp(new Item(items: [a.items[1]], bits: a.bits + '1'))
}

List<Item> stats(String s) {
    stringToSet(s).collect { new Item(items: [it], count: s.count(it)) }
}

Map<String, String> bitmap(List<Item> a) {
    a.collectEntries { [it.items[0], it.bits] }
}


List<String> huffmann(String s) {
    s.collect { bitmap(bottomUp(topDown(stats(s))[0]))[it] }
}

print huffmann('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbcccddeef')
