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
public class Spell_KnowOrigin extends Spell
{
	public String ID() { return "Spell_KnowOrigin"; }
	public String name(){return "Know Origin";}
	public int abstractQuality(){return INDIFFERENT;};
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}
	public int classificationCode(){ return Ability.SPELL|Ability.DOMAIN_DIVINATION;}

	public Room origin(MOB mob, Environmental meThang)
	{
		if(meThang instanceof LandTitle)
			return (Room)((LandTitle)meThang).getPropertyRooms().firstElement();
		else
		if(meThang instanceof MOB)
			return ((MOB)meThang).getStartRoom();
		else
		if(meThang instanceof Item)
		{
			Item me=(Item)meThang;
			try
			{
				// check mobs worn items first!
				for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
				{
					Room R=(Room)r.nextElement();
					if(CMLib.flags().canAccess(mob,R))
					{
						for(int s=0;s<R.numInhabitants();s++)
						{
							MOB M=R.fetchInhabitant(s);
							if((M!=null)
							&&(M.isMonster())
							&&(!(M instanceof ShopKeeper))
							&&(M.fetchInventory(me.Name())!=null)
							&&(!M.fetchInventory(me.Name()).amWearingAt(Item.IN_INVENTORY)))
								return M.getStartRoom();
						}
					}
				}
		    }catch(NoSuchElementException nse){}
		    try
		    {
				// check shopkeepers second!
				for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
				{
					Room R=(Room)r.nextElement();
					if(CMLib.flags().canAccess(mob,R))
					{
						for(int s=0;s<R.numInhabitants();s++)
						{
							MOB M=R.fetchInhabitant(s);
							if((M!=null)&&(CMLib.coffeeShops().getShopKeeper(M)!=null))
							{
								ShopKeeper S=CMLib.coffeeShops().getShopKeeper(M);
								if(S.getShop().doIHaveThisInStock(me.Name(),null,S.whatIsSold(),M.getStartRoom()))
									return M.getStartRoom();
							}
						}
					}
				}
		    }catch(NoSuchElementException nse){}
		    try
		    {
				// check mobs inventory items third!
				for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
				{
					Room R=(Room)r.nextElement();
					if(R!=null)
					{
						for(int s=0;s<R.numInhabitants();s++)
						{
							MOB M=R.fetchInhabitant(s);
							if((M!=null)
							&&(M.isMonster())
							&&(!(M instanceof ShopKeeper))
							&&(M.fetchInventory(me.Name())!=null)
							&&(M.fetchInventory(me.Name()).amWearingAt(Item.IN_INVENTORY)))
								return M.getStartRoom();
						}
					}
				}
		    }catch(NoSuchElementException nse){}
		    try
		    {
				// check room stuff last
				for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
				{
					Room R=(Room)r.nextElement();
					if((CMLib.flags().canAccess(mob,R))&&(R.fetchItem(null,me.Name())!=null))
					   return R;
				}
		    }catch(NoSuchElementException nse){}
		}
		return null;
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		Environmental target=getAnyTarget(mob,commands,givenTarget,Item.WORNREQ_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		Room R=origin(mob,target);
		boolean success=profficiencyCheck(mob,0,auto);
		if((success)&&(R!=null))
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":"^S<S-NAME> incant(s), divining the origin of <T-NAMESELF>.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.tell(target.name()+" seems to come from '"+R.roomTitle()+"'.");
			}
		}
		else
			beneficialWordsFizzle(mob,target,"<S-NAME> attempt(s) to divine something, but fail(s).");

		return success;
	}
}
