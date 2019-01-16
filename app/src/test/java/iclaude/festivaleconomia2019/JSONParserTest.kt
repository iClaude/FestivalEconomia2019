package iclaude.festivaleconomia2019

import iclaude.festivaleconomia2019.model.JSONparser.JSONparser
import iclaude.festivaleconomia2019.model.data_classes.hasRelatedSessions
import iclaude.festivaleconomia2019.model.data_classes.hasWebsiteUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import org.hamcrest.CoreMatchers.*
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.text.IsEmptyString
import org.hamcrest.text.IsEqualIgnoringCase
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Testing JSON parsing of the event data in the file event_data_2019.json
 * */

private const val JSON_FILE_NAME = "event_data_2019.json"

class JSONParserTest {
    @Test
    fun testJsonParser() {
        val inputStream = this.javaClass.classLoader.getResource(JSON_FILE_NAME).openStream()
        val eventData = JSONparser.parseEventData(inputStream = inputStream)

        // number of objects
        assertThat(eventData.locations.size, Is(equalTo(2)))
        assertThat(eventData.sessions.size, Is(equalTo(2)))
        assertThat(eventData.organizers.size, Is(equalTo(2)))
        assertThat(eventData.tags.size, Is(equalTo(4)))

        // locations
        assertThat(eventData.locations[0].name, Is(equalTo("MUSE")))
        assertThat(eventData.locations[0].lat, Is(equalTo(46.062627)))
        assertThat(eventData.locations[1].description, containsString("Faculty of Economics"))
        assertThat(eventData.locations[1].lng, Is(equalTo(11.117670)))

        // sessions
        assertThat(eventData.sessions[0].location, Is(equalTo("01")))
        assertThat(eventData.sessions[0].relatedSessions?.size, Is(equalTo(0)))
        assertThat(eventData.sessions[0].hasRelatedSessions(), Is(equalTo(false)))
        assertThat(eventData.sessions[0].tags1, hasItem("Keynote"))
        assertThat(eventData.sessions[1].youtubeUrl, nullValue())
        assertThat(eventData.sessions[1].hasYoutubeUrl(), Is(equalTo(false)))
        assertThat(eventData.sessions[1].organizers[0], Is(equalTo("02")))

        // organizers
        assertThat(eventData.organizers[0].name, Is(equalTo("Izabela Orlowska")))
        assertThat(eventData.organizers[0].twitterUrl, containsString("SandeepDinesh"))
        assertThat(eventData.organizers[0].bio, containsString("Izabela"))
        assertThat(eventData.organizers[1].websiteUrl, IsEmptyString())
        assertThat(eventData.organizers[1].hasWebsiteUrl(), Is(equalTo(false)))

        // tags
        assertThat(eventData.tags[0].name, Is(equalTo("Keynote")))
        assertThat(eventData.tags[1].color, IsEqualIgnoringCase("#cce9c2"))
        assertThat(eventData.tags[3].fontColor, IsEqualIgnoringCase("#202124"))
    }
}
