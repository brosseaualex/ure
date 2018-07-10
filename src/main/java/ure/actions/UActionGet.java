package ure.actions;

import ure.UCommander;
import ure.actors.UActor;
import ure.things.UThing;

import javax.inject.Inject;

/**
 * Actor tries to pick up the thing, from the ground or a container.
 *
 */
public class UActionGet extends UAction {

    @Inject
    UCommander commander;

    public static String id = "GET";

    public static String nothingToGetMsg = "There's nothing here to get.";

    UThing thing;

    public UActionGet(UActor theactor) {
        actor = theactor;
        thing = null;
    }

    public UActionGet(UActor theactor, UThing thething) {
        actor = theactor;
        thing = thething;
    }

    @Override
    void doMe() {
        if (thing == null) {
            thing = actor.myCell().topThingAt();
            if (thing == null) {
                if (actor.isPlayer()) {
                    //commander.printScroll(nothingToGetMsg);
                }
            }
        }
        if (!actor.tryGetThing(thing))
            suppressEvent();
    }
}
