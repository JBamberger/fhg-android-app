package de.jbamberger.fhg.repository.data

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlValue

/**
 * RSS model class according to the rss specification 2.0 {@link https://cyber.harvard.edu/rss/rss.html}
 *
 * @param version rss version (required)
 * @param channel channel element
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 * @version 2.0
 */
@XmlRootElement(name = "rss")
internal data class Rss(
        @XmlAttribute(name = "version", required = true) val version: String,
        @XmlElement(name = "channel", required = true) val channels: Channel)

/**
 * @param title channel name
 * @param link url of the html page
 * @param description description of the channel
 * @param language language of the channel (https://www.w3.org/TR/REC-html40/struct/dirlang.html#langcodes)
 * @param copyright copyright notice for the content in the channel
 * @param managingEditor email + name of person responsible for the editorial content
 * @param webMaster email + name of the person responsible for the technical side of the channel
 * @param pubDate publication date (RFC 822 but year might be 2 or four characters)
 * @param lastBuildDate last time the content changed (RFC 822 but year might be 2 or four characters)
 * @param category channel category
 * @param generator indicates the program that generated the content
 * @param docs url pointing to the format specification
 * @param cloud information on how to subscribe to updates
 * @param ttl time to live in minutes (cache duration)
 * @param image image associated with the channel (one of GIF, JPEG, PNG)
 * @param rating PICS rating of the content (https://www.w3.org/PICS/)
 * @param textInput text input box that can appear with the content
 * @param skipHours hint, which hours can be skipped
 * @param skipDays hint, which days can be skipped
 * @param items any number of Items contained in the channel
 */
internal data class Channel(
        @XmlElement(name = "title", required = true) val title: String,
        @XmlElement(name = "link", required = true) val link: String,
        @XmlElement(name = "description", required = true) val description: String,
        @XmlElement(name = "language", required = false) val language: String?,
        @XmlElement(name = "copyright", required = false) val copyright: String?,
        @XmlElement(name = "managingEditor", required = false) val managingEditor: String?,
        @XmlElement(name = "webMaster", required = false) val webMaster: String?,
        @XmlElement(name = "pubDate", required = false) val pubDate: String?,
        @XmlElement(name = "lastBuildDate", required = false) val lastBuildDate: String?,
        @XmlElement(name = "category", required = false) val category: Category?,
        @XmlElement(name = "generator", required = false) val generator: String?,
        @XmlElement(name = "docs", required = false) val docs: String?,
        @XmlElement(name = "cloud", required = false) val cloud: Cloud?,
        @XmlElement(name = "ttl", required = false) val ttl: String?,
        @XmlElement(name = "image", required = false) val image: Image?,
        @XmlElement(name = "rating", required = false) val rating: String?,
        @XmlElement(name = "textInput", required = false) val textInput: TextInput?,
        @XmlElement(name = "skipHours", required = false) val skipHours: SkipHours?,
        @XmlElement(name = "skipDays", required = false) val skipDays: SkipDays?,
        @XmlElement val items: List<Item>)

/**
 * @param url url of an image (one of GIF, PNG, JPEG)
 * @param title description of the image (alt text)
 * @param link url of the page when the channel is rendered
 * @param width width of the image in pixels (max: 144, default: 88)
 * @param height height of the image in pixels (max: 400, default: 31)
 * @param description text for the title tag when rendered
 */
internal data class Image(
        @XmlElement(name = "url", required = true) val url: String,
        @XmlElement(name = "title", required = true) val title: String,
        @XmlElement(name = "link", required = true) val link: String,
        @XmlElement(name = "width", required = false) val width: Int?,
        @XmlElement(name = "height", required = false) val height: Int?,
        @XmlElement(name = "description", required = false) val description: String?)

/**
 * Specifies a web service that supports the rssCloud interface via HTTP-POST, XML-RPC or SOAP 1.1
 */
internal data class Cloud(
        @XmlAttribute(name = "domain", required = true) val domain: String,
        @XmlAttribute(name = "port", required = true) val port: Int,
        @XmlAttribute(name = "path", required = true) val path: String,
        @XmlAttribute(name = "registerProcedure", required = true) val registerProcedure: String,
        @XmlAttribute(name = "protocol", required = true) val protocol: String)

/**
 * @param title label of the submit button
 * @param description explanation of the input field
 * @param name name of the text object in the input area
 * @param link URL of the GCI script that processes the text input
 */
internal data class TextInput(
        @XmlElement(name = "title", required = true) val title: String,
        @XmlElement(name = "description", required = true) val description: String,
        @XmlElement(name = "name", required = true) val name: String,
        @XmlElement(name = "link", required = true) val link: String)

/**
 * One feed entry.
 * Contains at least one of title and description.
 *
 * @param title title of the item
 * @param link link to the full item text
 * @param description synopsis or full text of the item (possibly entity-encoded html)
 * @param author email + name of the author
 * @param category category of the item
 * @param comments url to the page of comments for this item
 * @param enclosure media item that is attached to the item
 * @param guid string that uniquely identifies the item
 * @param pubDate date at which the item was published (RFC 822 but year might be 2 or four characters)
 * @param source the rss channel the item came from
 */
internal data class Item(
        @XmlElement(name = "title", required = false) val title: String?,
        @XmlElement(name = "link", required = false) val link: String?,
        @XmlElement(name = "description", required = false) val description: String?,
        @XmlElement(name = "author", required = false) val author: String?,
        @XmlElement(name = "category", required = false) val category: Category?,
        @XmlElement(name = "comments", required = false) val comments: String?,
        @XmlElement(name = "enclosure", required = false) val enclosure: Enclosure?,
        @XmlElement(name = "guid", required = false) val guid: Guid?,
        @XmlElement(name = "pubDate", required = false) val pubDate: String?,
        @XmlElement(name = "source", required = false) val source: Source?)

/**
 * @param url url of the originating rss channel
 * @param title human readable description of the channel
 */
internal data class Source(
        @XmlAttribute(name = "url", required = true) val url: String,
        @XmlValue val title: String)

/**
 * @param url location of the enclousure
 * @param length size in bytes
 * @param type mime type of the object
 */
internal data class Enclosure(
        @XmlAttribute(name = "url", required = true) val url: String,
        @XmlAttribute(name = "length", required = true) val length: String,
        @XmlAttribute(name = "type", required = true) val type: String)

/**
 * @param domain url of a taxonomy
 * @param categories string of categories
 */
internal data class Category(
        @XmlAttribute(name = "domain", required = false) val domain: String?,
        @XmlValue val categories: String)

/**
 * @param isPermaLink true, if the value is a permalink to the post. One of true and false
 * @param guid unique identifier of the post. Possibly a permalink.
 */
internal data class Guid(
        @XmlAttribute(name = "isPermaLink", required = false) val isPermaLink: String?,
        @XmlValue val guid: String)

/**
 * Hours of the day when the channel is not updated.
 *
 * @param hours up to twenty-four hour elements.
 */
internal data class SkipHours(@XmlElement val hours: List<Hour>)

/**
 * @param hour hour of the day in GMT (one of 0..23)
 */
internal data class Hour(@XmlValue val hour: Int)

/**
 * Days of the week when the channel is not updated.
 *
 * @param days up to seven day elements.
 */
internal data class SkipDays(@XmlElement val days: List<Day>)

/**
 * @param name name of the day (one of Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday)
 */
internal data class Day(@XmlValue val name: String)
