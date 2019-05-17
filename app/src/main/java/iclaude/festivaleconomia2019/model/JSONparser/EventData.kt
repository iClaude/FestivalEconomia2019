package iclaude.festivaleconomia2019.model.JSONparser

import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Organizer
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.model.data_classes.Tag

data class EventData(
        val locations: List<Location>,
        val sessions: MutableList<Session>,
        val organizers: List<Organizer>,
        val tags: List<Tag>
)