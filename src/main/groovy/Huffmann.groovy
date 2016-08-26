List<Map> tail2(List<Map> maplist) {
    (maplist.size() > 2) ? maplist[2..-1] : []
}

List<Map> topDown(List<Map> maplist) {
    topDownSorted(maplist.sort { it.count } )
}

List<Map> topDownSorted(List<Map> maplist) {
    (maplist.size() == 1) ?
            maplist :
            topDown(tail2(maplist) + [items: [maplist[0], maplist[1]], count: maplist[0].count + maplist[1].count, bits: ''])
}

Set<String> stringToSet(String s) {
    new HashSet(s.split('') as List)
}

List<Map> bottomUpSingleItem(Map m) {
    (m.items[0] instanceof Map) ?
            bottomUp([items: m.items[0].items, bits: m.bits]) :
            [m]
}

List<Map> bottomUp(Map m) {
    (m.items.size() == 1) ?
            bottomUpSingleItem(m) :
            bottomUp([items: [m.items[0]], bits: m.bits + '0']) +
            bottomUp([items: [m.items[1]], bits: m.bits + '1'])
}

List<Map> stats(String s) {
    stringToSet(s).collect { [items: [it], count: s.count(it), bits: ''] }
}

Map<String, String> bitmap(List<Map> maplist) {
    maplist.collectEntries { [it.items[0], it.bits] }
}

List<String> huffmann(String s) {
    s.collect { bitmap(bottomUp(topDown(stats(s))[0]))[it] }
}

def s = 'aaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbcdefghij'
println huffmann(s)
