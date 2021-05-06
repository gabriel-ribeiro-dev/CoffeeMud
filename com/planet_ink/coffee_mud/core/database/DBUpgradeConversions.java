package com.planet_ink.coffee_mud.core.database;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
   Copyright 2014-2021 Bo Zimmerman

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
public class DBUpgradeConversions
{
	private void pl(final PrintStream out, final String str)
	{
		if(out!=null)
			out.println(str);
	}

	private void p(final PrintStream out, final String str)
	{
		if(out!=null)
			out.print(str);
	}


	private boolean needToUpgradeBacklogIndexes(final Map<String,List<String>> oldTables)
	{
		if(oldTables.containsKey("CMBKLG"))
		{
			final List<String> backLogTables = oldTables.get("CMBKLG");
			if(!backLogTables.contains("#CMSNAM"))
				return true;
		}
		return false;
	}
	
	public void DBUpgradeTableSort(final List<String> tableList, 
								   final Map<String,List<String>> oldTables, 
								   final PrintStream out)
	{
		if(needToUpgradeBacklogIndexes(oldTables))
		{
			tableList.remove("CMCLAN");
			tableList.remove("CMCHCL");
			tableList.add(0, "CMCHCL");
			tableList.add(0, "CMCLAN");
		}
	}

	//private final List<String>				clans		= new LinkedList<String>();
	//private final Map<String, List<String>>	clanLinks	= new TreeMap<String, List<String>>();
	
	public void DBUpgradeConversionV1(final Map<String,List<String>> oldTables,
									  final Map<String,List<String>> newTables,
									  final Map<String,List<List<String>>> data,
									  final PrintStream out)
	{
		// first, look for the CLAN conversion
		if(newTables.containsKey("CMCHCL") && (!oldTables.containsKey("CMCHCL")))
		{
			final List<List<String>> charRows=data.get("CMCHAR");
			if(charRows != null)
			{
				List<List<String>> cmchclRows=data.get("CMCHCL");
				if(cmchclRows==null)
				{
					cmchclRows=new ArrayList<List<String>>();
					data.put("CMCHCL", cmchclRows);
				}
				pl(out," ");
				pl(out," ");
				p(out,"Making CMCHCL conversion: ");
				final List<String> ofields=oldTables.get("CMCHAR");
				final List<String> nfields=newTables.get("CMCHCL");
				final int cmUserIDIndex=ofields.indexOf("$CMUSERID");
				final int cmClanIndex=ofields.indexOf("$CMCLAN");
				final int cmClRoIndex=ofields.indexOf("#CMCLRO");
				final int cm2UserIDIndex=nfields.indexOf("$CMUSERID");
				final int cm2ClanIndex=nfields.indexOf("$CMCLAN");
				final int cm2ClRoIndex=nfields.indexOf("#CMCLRO");
				for(int r=0;r<charRows.size();r++)
				{
					final List<String> row=charRows.get(r);
					final String userID=row.get(cmUserIDIndex);
					final String clanID=row.get(cmClanIndex);
					final String clanRo=row.get(cmClRoIndex);
					if((clanID==null)||(clanID.length()==0))
						continue;
					final ArrayList<String> newRow=new ArrayList<String>(3);
					newRow.add(""); newRow.add(""); newRow.add("");
					newRow.add(cm2UserIDIndex,userID);
					newRow.add(cm2ClanIndex,clanID);
					newRow.add(cm2ClRoIndex,clanRo);
					cmchclRows.add(newRow);
				}
				if(cmchclRows.size()>0)
					oldTables.put("CMCHCL", newTables.get("CMCHCL"));
			}
		}

		// now look for cmchid insertion
		if(newTables.containsKey("CMCHAR") && (oldTables.containsKey("CMCHAR")))
		{
			final List<List<String>> charRows=data.get("CMCHAR");
			if(charRows != null)
			{
				final List<String> ofields=oldTables.get("CMCHAR");
				final List<String> nfields=newTables.get("CMCHAR");
				if(nfields.contains("$CMCHID") && (!ofields.contains("$CMCHID")))
				{
					ofields.add("$CMCHID");
					pl(out," ");
					pl(out," ");
					p(out,"Making CMCHID conversion: ");
					for(int r=0;r<charRows.size();r++)
					{
						final List<String> row=charRows.get(r);
						row.add("StdMOB");
					}
				}
			}
		}
		/*
		if(needToUpgradeBacklogIndexes(oldTables) && newTables.containsKey("CMCHCL"))
		{
			final List<List<String>> clanRows=data.get("CMCLAN");
			if(clanRows != null)
			{
				int x=oldTables.get("CMCLAN").indexOf("$CMCLID");
				for(int r=0;r<clanRows.size();r++)
				{
					final List<String> row=clanRows.get(r);
					if(!clans.contains(row.get(x)))
						clans.add(row.get(x));
				}
			}
			final List<List<String>> chclRows=data.get("CMCHCL");
			if(chclRows != null)
			{
				int x=oldTables.get("CMCHCL").indexOf("$CMUSERID");
				int y=oldTables.get("CMCHCL").indexOf("$CMCLAN");
				for(int r=0;r<chclRows.size();r++)
				{
					final List<String> row=chclRows.get(r);
					if(!clanLinks.containsKey(row.get(x)))
						clanLinks.put(row.get(x), new ArrayList<String>(4));
					clanLinks.get(row.get(x)).add(row.get(y));
				}
			}
			final List<List<String>> bklgRows=data.get("CMBKLG");
			if(chclRows != null)
			{
			}
		}
		*/
	}
}
