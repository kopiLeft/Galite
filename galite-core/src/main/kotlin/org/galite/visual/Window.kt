package org.galite.visual

import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.galite.base.Application
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import org.vaadin.examples.form.menu.actorBar.menubar.ActorBar

open class Window(title : String, actors: List<Actor>, application: Application) : VerticalLayout() {
    init {
        isPadding = false
        style.set("margin", "0");
        var titre = H3(title)
        titre.style.set("padding-left","2%")
        add(titre)
        var actorBar = ActorBar(actors)
        add(actorBar)
        quitCommande(actorBar, application)
    }

    /**
     * Quit the current window.
     * @param actors The list of actor attached to window.
     */
    fun quitCommande(actors : ActorBar, application: Application ){
        var quitindex = actors.menuBar.items.map { menuItem -> menuItem.text }.indexOf("Quit")
        var QuitActor = actors.menuBar.items[quitindex]
        QuitActor.addClickListener{e ->
            application.removeWindow(e.source.parent.get().parent.get().parent.get())
        }
    }

}