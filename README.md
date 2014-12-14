Grails Url Shortener
====================

[![Build Status](https://drone.io/github.com/lmivan/grails-url-shortener/status.png)](https://drone.io/github.com/lmivan/grails-url-shortener/latest)
![Still maintained](http://stillmaintained.com/lmivan/grails-url-shortener.png "http://stillmaintained.com/lmivan/grails-url-shortener")
[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/lmivan/grails-url-shortener/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

This is a grails plugin that integrates an url shortener inside your Grails application.


## Installation

In `BuildConfig` add the following dependency:

```groovy
compile (":url-shortener:<version>")
```


## Configuration

After installing the plugin you have to add the following configuration to your `Config.groovy` file:

```groovy
// Grails-url-shortener
shortener {
    characters = ('0'..'9') + ('a'..'h') + ('j'..'k') + ('m'..'z') + ('A'..'H') + ('J'..'K') + ('M'..'Z')
    minLength = 5
    shortDomain = http://YOUR-SHORT-DOMAIN.COM
}
```

- `characters` the list of the valid characters for the shorted urls. In this example, the letters `i`, `l`, `I`, `L` are removed because they can be mix up with some typographies. You can create your custom list, for example, removing the vowels.
- `minLength` is the minimum number of characters for the shorted url.
- `shortDomain` the short domain for your url shortener.

Next you have to provide a unique number generator. The plugin provides an in-memory dummy implementation using AtomicLong available in the file [DummySequenceGenerator](https://github.com/lmivan/grails-url-shortener/blob/master/src/groovy/net/kaleidos/shortener/generator/DummySequenceGenerator.groovy).


To implement your custom unique number generator you have to create a simple Groovy or Java class that implements the `net.kaleidos.shortener.SequenceGenerator` interface. For example you can use a database sequence for this number generator. The following class use a Postgresql sequence for this:

```groovy
// File: /src/groovy/com/example/shortener/PostgresSequenceGenerator.groovy
package com.example.shortener

import net.kaleidos.shortener.SequenceGenerator
import groovy.sql.Sql

class PostgresSequenceGenerator implements SequenceGenerator {
    def dataSource

    Long getNextNumber() {
        def db = new Sql(dataSource)
        return db.rows("SELECT nextval('seq_shorten_url_generator')")[0]['nextval']
    }
}
```

Finally, when you have created you custom generator you have to define the `sequenceGenerator` bean in your `resources.groovy` file:

```groovy
sequenceGenerator(com.example.shortener.PostgresSequenceGenerator) {
    dataSource = ref("dataSource")
}
```

### Sequence Generator Plugin

You can also use the [sequence-generator](http://grails.org/plugin/sequence-generator) plugin to generate sequence numbers for short urls.
Version 1.1+ of `sequence-generator` is compatible with version 0.2+ of `url-shortener`. Just add the `sequence-generator` plugin to
your BuildConfig.groovy and you are ready to go.
You don't have to create a custom generator or configure a sequenceGenerator bean (section above), it done by the `sequence-generator` plugin.

## Usage

The plugin provides a service `urlShortenerService` that you can inject into your grails artefacts with the following public methods:

- `shortUrl(String targetUrl)`: Returns a short url for the target url passed as param. If the url has already been shortened, the same short url is returned. Be careful because this method only returns the shortened characters for the target url but not the url with the configured short domain.
- `shortUrlFullDomain(String targetUrl)`: Returns the short url with the full domain.
- `getTargetUrl(String shortUrl)`: Returns the target url (original url) from the short url passed as param. The method also increase a counter with the number of times this url has been hit. If the short url does not exist the method return null.

Examples:

```groovy
String shortUrl = urlShortenerService.shortUrl("http://kaleidos.net")
assert shortUrl.length() == 5

String shortUrlWithDomain = urlShortenerService.shortUrlFullDomain("http://kaleidos.net")
assert shortUrlWithDomain.length() > 5
assert shortUrlWithDomain.contains("http://") == true

String shortUrl = urlShortenerService.shortUrl("http://kaleidos.net")
assert urlShortenerService.getTargetUrl(shortUrl) == "http://kaleidos.net"
```

The plugin also provides a controller that can be used to redirect to the target url. It is available here [ShortenerController](https://github.com/lmivan/grails-url-shortener/blob/master/grails-app/controllers/net/kaleidos/shortener/ShorternerController.groovy) or you can implement your own custom controller.

### Tag Library

The plugin provides two GSP tags that generate short urls, `link` and `createLink`. They work as the standard Grails `link` and `createLink` but generates short urls.

```javascript
function copyShortLinkToClipboard() {
    var url = "${shorter.createLink(controller: 'person', action: 'show', id: person.id, absolute: true)}";
    window.prompt("${message(code: 'copy.to.clipboard.label', 'Copy to clipboard: Ctrl+C, Enter')}", url);
}
```

## Author

You can send any questions to:

Iván López: lopez.ivan@gmail.com ([@ilopmar](https://twitter.com/ilopmar))

Collaborations are appreciated :-)


## Release Notes

* 0.2 - 21/Nov/2014 - Added GSP tags `shorter:link` and `shorter:createLink`.
* 0.1 - 17/Oct/2013 - Initial version of the plugin.