package net.kaleidos.shortener

class ShortenerTagLib {

    static namespace = "shorter"

    def urlShortenerService

    private static final List EXCLUDE = ['action', 'controller', 'id', 'params']

    private Map makeUri(Map attrs) {
        def map = [:]
        for (entry in attrs) {
            if (!EXCLUDE.contains(entry.key)) {
                map[entry.key] = entry.value
            }
        }
        map
    }

    def link = { Map attrs, Closure body ->
        def longLink = g.createLink(attrs).toString()
        def shortLink = urlShortenerService.shortUrlFullDomain(longLink)
        def tmp = makeUri(attrs)
        tmp.uri = shortLink
        out << g.link(tmp, body)
    }

    def createLink = { Map attrs ->
        def longLink = g.createLink(attrs).toString()
        def shortLink = urlShortenerService.shortUrlFullDomain(longLink)
        def tmp = makeUri(attrs)
        tmp.uri = shortLink
        out << g.createLink(tmp)
    }
}