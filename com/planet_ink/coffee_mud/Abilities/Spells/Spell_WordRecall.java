package com.planet_ink.coffee_mud.Abilities.Spells;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class Spell_WordRecall extends Spell
{
    public String ID() { return "Spell_WordRecall"; }
    public String name(){ return "Word of Recall";}
    protected int canAffectCode(){return 0;}
    protected int canTargetCode(){return 0;}
    public int abstractQuality(){return Ability.INDIFFERENT;}
    public int classificationCode(){ return Ability.SPELL|Ability.DOMAIN_CONJURATION;}
    protected int overrideMana(){return Integer.MAX_VALUE-90;}
    public long flags(){return Ability.FLAG_TRANSPORTING;}

    protected int verbalCastCode(MOB mob, Environmental target, boolean auto)
    {
        int affectType=CMMsg.MSK_CAST_VERBAL|CMMsg.TYP_RECALL;
        if(auto) affectType=affectType|CMMsg.MASK_GENERAL;
        return affectType;
    }

    public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
    {
        if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
            return false;

        boolean success=(!mob.isInCombat())||profficiencyCheck(mob,0,auto);
        if(success)
        {
            int AUTO=auto?CMMsg.MASK_GENERAL:0;
            Room recalledRoom=mob.location();
            Room recallRoom=mob.getStartRoom();
            CMMsg msg=CMClass.getMsg(mob,recalledRoom,this,verbalCastCode(mob,recalledRoom,auto),CMMsg.MASK_MAGIC|AUTO|CMMsg.MSG_LEAVE,verbalCastCode(mob,recalledRoom,auto),auto?getScr(ID(),"recallgo1"):getScr(ID(),"recallgo2"));
            CMMsg msg2=CMClass.getMsg(mob,recallRoom,this,verbalCastCode(mob,recallRoom,auto),CMMsg.MASK_MAGIC|AUTO|CMMsg.MASK_MOVE|CMMsg.MSG_ENTER,verbalCastCode(mob,recallRoom,auto),null);
            if((recalledRoom.okMessage(mob,msg))&&(recallRoom.okMessage(mob,msg2)))
            {
                recalledRoom.send(mob,msg);
                recallRoom.send(mob,msg2);
                if(recalledRoom.isInhabitant(mob))
                    recallRoom.bringMobHere(mob,false);
                for(int f=0;f<mob.numFollowers();f++)
                {
                    MOB follower=mob.fetchFollower(f);
                    
                    msg=CMClass.getMsg(follower,recalledRoom,this,verbalCastCode(mob,recalledRoom,auto),CMMsg.MASK_MAGIC|AUTO|CMMsg.MSG_LEAVE,verbalCastCode(mob,recalledRoom,auto),auto?getScr(ID(),"recallgo1"):getScr(ID(),"recallgo3",mob.name()));
                    if((follower!=null)
                    &&(follower.isMonster())
                    &&(!follower.isPossessing())
                    &&(follower.location()==recalledRoom)
                    &&(recalledRoom.isInhabitant(follower))
                    &&(recalledRoom.okMessage(follower,msg)))
                    {
                        msg2=CMClass.getMsg(follower,recallRoom,this,verbalCastCode(mob,recallRoom,auto),CMMsg.MASK_MAGIC|AUTO|CMMsg.MASK_MOVE|CMMsg.MSG_ENTER,verbalCastCode(mob,recallRoom,auto),null);
                        if(recallRoom.okMessage(follower,msg2))
                        {
                            recallRoom.send(follower,msg2);
                            if(recalledRoom.isInhabitant(follower))
                                recallRoom.bringMobHere(follower,false);
                        }
                    }
                }
            }
        }
        else
            beneficialWordsFizzle(mob,null,getScr(ID(),"recallgo4"));

        // return whether it worked
        return success;
    }

}
