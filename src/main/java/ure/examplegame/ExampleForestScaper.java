package ure.examplegame;

import ure.areas.*;

public class ExampleForestScaper extends ULandscaper {

    public static final String TYPE = "forestscaper";

    public ExampleForestScaper() {
        super(TYPE);
    }

    @Override
    public void buildArea(UArea area, int level, String[] tags) {

        fillRect(area, "tree", 0, 0, area.xsize-1, area.ysize-1);

        // Dig a network of big grass spaces.
        Shapemask grass = shapeCaves(area.xsize-4, area.ysize-4, 0.38f, 4, 2, 3);
        grass.grow(1).writeTerrain(area, "grass", 1, 1);

        // Add some holes in the tree clumps, some random lone trees and saplings, and a fringe of saplings
        grass.copy().invert().edges().writeTerrain(area, "sapling", 1 ,1, 0.5f);
        grass.writeTerrain(area, "tree", 1 ,1, 0.03f);
        grass.copy().edgesThick().writeTerrain(area, "sapling", 1, 1, 0.6f);
        grass.writeTerrain(area, "sapling", 1, 1, 0.05f);

        // Lakes
        int lakes = rand(10)+4;
        Shapemask lakemask = new Shapemask(area.xsize+4, area.ysize+4);
        for (int i=0;i<lakes;i++) {
            int lakew = 7+rand(25); int lakeh = 7+rand(25);
            Shapemask lake = shapeBlob(lakew,lakeh);
            int x = rand(area.xsize);
            int y = rand(area.ysize);
            lake.writeTerrain(area, "water", x, y);
            lakemask.maskWith(lake, Shapemask.MASKTYPE_OR, x, y);
            if (randf() < 0.5f) {
                Shapemask mud = lake.copy().grow(1 + rand(4)).erode(0.6f, 1 + rand(3)).maskWith(lake, Shapemask.MASKTYPE_NOT);
                mud.writeTerrain(area, "mud", x, y, 0.9f);
            }
            lake.shrink(rand(3)).smooth(7,1+rand(3)).writeTerrain(area, "deep water", x, y);
            scatterActorsByTags(area, x-1,y-1,x+lakew,y+lakeh, new String[]{"forest"}, 4, 2);
        }

        // Run some roads through the map.  Save them all in one mask for later.
        int roads = rand(2)+2;
        Shapemask roadmask = new Shapemask(area.xsize+4,area.ysize+4);
        for (int i=0;i<roads;i++) {
            Shapemask road = shapeRoad(area.xsize +4, area.ysize +4, 2, 1.2f, 1.3f);
            roadmask.maskWith(road, Shapemask.MASKTYPE_OR);
            road.grow(1).edges().writeTerrain(area, "grass", -2, -2, 0.7f);
            road.sparsen(22, 36).writeThings(area, "lamppost", -2, -2);
        }

        // Run some rivers through the map.  Save them all in one mask for later.
        int rivers = rand(3)+1;
        Shapemask rivermask = new Shapemask(area.xsize+4, area.ysize+4);
        for (int i=0;i<rivers;i++) {
            Shapemask river = shapeRoad(area.xsize+4, area.ysize+4, 5, 1.4f, 1.6f);
            river.writeTerrain(area, "water", -2, -2);
            rivermask.maskWith(river, Shapemask.MASKTYPE_OR);
            river.shrink(2).writeTerrain(area, "deep water", -2, -2, 0.8f);
        }

        // Add banks on the edges of rivers.
        Shapemask banks = rivermask.copy().grow(1).edges().maskWith(lakemask, Shapemask.MASKTYPE_NOT);
        banks.writeTerrain(area, "sand", -2, -2);
        banks.writeTerrain(area, "grass", -2, -2, 0.1f);

        // Rock formations and caves
        int caves = rand(3)+3;
        int rocks = rand(15)+5;
        for (int i=0;i<rocks;i++) {
            Shapemask rock = shapeOddBlob(10+rand(25),10+rand(25),3, 0.3f);
            int x = rand(area.xsize - 10) + 5;
            int y = rand(area.ysize - 10) + 5;
            rock.writeTerrain(area, "rock", x, y, 1f);
            if (i < caves) {
                UCell caveCell = findAnextToB(area, "rock", "grass", x,y,x+rock.xsize,y+rock.ysize);
                if (caveCell != null) {
                    URegion caveRegion = makeCaveRegion();
                    linkRegionAt(area, caveCell.x, caveCell.y, "cave entrance", caveRegion, 1, "cave exit");
                }
                scatterActorsByTags(area, x-3, y-3,x+rock.xsize+3, y+rock.ysize+3, new String[]{"rock"}, 1, 1+rand(3));
                scatterThingsByTags(area, x-3, y-3,x+rock.xsize+3, y+rock.ysize+3, new String[]{"rock"}, 1, 1+rand(3));
            }
        }

        // Draw the roads now, after their edges are drawn, so we go over those
        roadmask.writeTerrain(area, "dirt", -2, -2);

        // Combine the roads and rivers to get bridges
        rivermask.maskWith(roadmask, Shapemask.MASKTYPE_AND);
        rivermask.writeTerrain(area, "wooden bridge", -2 ,-2, 0.97f);



        scatterThingsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 50);
        scatterActorsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 30);
    }

    public URegion makeCaveRegion() {
        String[] names = new String[]{"Caverns of pain", "Terror cave", "Mystery cave", "Caverns of Night", "Horror cave"};
        String name = names[rand(names.length)];
        String id = "cavern-" + Integer.toString(rand(100000));
        URegion region = new URegion(id, name, new ULandscaper[]{new ExampleCaveScaper()},
                        new String[]{"cave"}, 90, 90, rand(5)+3,
                        "cave entrance", "cave exit", "sounds/ultima_dungeon.ogg");
        return region;
    }








    public void oldBuildArea(UArea area, int level, String[] tags) {
        fillRect(area, "grass", 0,0,area.xsize-1,area.ysize-1);
        simplexScatterTerrain(area, "tree", new String[]{"grass"}, 0.4f, 0.7f, new float[]{2f,7f,13f});
        digCaves(area, "grass", 0,0,area.xsize-1,area.ysize-1, 0.41f, 5, 3, 1);
        simplexScatterTerrain(area, "sapling", new String[]{"grass"}, 0.35f + randf(0.3f), 0.04f + randf(0.1f), new float[]{4f,6f,12f});
        simplexScatterTerrain(area, "grass", new String[]{"tree"}, 0.1f + randf(0.4f), randf(0.25f), new float[]{5f,10f,38f});
        for (int i=0;i<rand(8);i++) {
            digRiver(area, "dirt", 0, 0, area.xsize - 1, area.ysize - 1, 1f + randf(2f), 1.3f, 1.3f);
        }
        for (int i=0;i<rand(3);i++) {
            digRiver(area, "water", 0,0,area.xsize-1,area.ysize-1, 2f + randf(5f), 1.5f, 2f);
        }
        for (int i=0;i<rand(4);i++) {
            System.out.println("digging lake");
            int lakew = 7 + rand(25);
            int lakeh = 7 + rand(25);
            UCell lakeloc = findAreaWithout(area, 1, 1, area.xsize - lakew - 2, area.ysize - lakeh - 2, lakew, lakeh, new String[]{"dirt"});
            if (lakeloc != null) {
                digCaves(area, "water", lakeloc.x, lakeloc.y, lakeloc.x + lakew, lakeloc.y + lakeh, 0.49f, 4, 5, 5);
            }
            scatterActorsByTags(area, lakeloc.x-1,lakeloc.y-1,lakeloc.x+lakew,lakeloc.y+lakeh, new String[]{"forest"}, 4, 2);
        }
        for (int i=0;i<rand(7)+5;i++) {
            System.out.println("building rock formation");
            int x1 = 10 + rand(area.xsize - 20);
            int y1 = 10 + rand(area.ysize - 20);
            int w = 7+rand(9);
            int h = 7+rand(9);
            digCaves(area, "rock", x1, y1, x1+w,y1+h,0.38f + randf(0.1f), 4+rand(3), 5, 3+rand(3));
            System.out.println("built rock formation!");
            if (randf() < 0.9f) {
                System.out.println("digging cave entrance");
                UCell doorcell = findAnextToB(area, "rock", "grass", x1, y1, x1 + w, y1 + h);
                if (doorcell != null) {
                    String[] names = new String[]{"Caverns of pain", "Terror cave", "Mystery cave", "Caverns of Night", "Horror cave"};
                    String name = names[rand(names.length)];
                    String id = "cavern-" + Integer.toString(doorcell.x*1000+doorcell.y);
                    URegion region = new URegion(id, name,
                            new ULandscaper[]{new ExampleCaveScaper()}, new String[]{"cave"}, 90, 90,
                            rand(5)+3, "cave entrance", "cave exit", "sounds/ultima_dungeon.ogg");
                    linkRegionAt(area, doorcell.x, doorcell.y, "cave entrance", region, 1, "cave exit");
                }
            }
            scatterActorsByTags(area, x1-3, y1-3,x1+w+3, y1+h+3, new String[]{"rock"}, 1, 1+rand(3));
            scatterThingsByTags(area, x1-3, y1-3,x1+w+3, y1+h+3, new String[]{"rock"}, 1, 1+rand(3));
        }
        for (int i=0;i<rand(4);i++) {
            int ruinw = 20+rand(20); int ruinh = 20+rand(20);
            UCell ruinloc = findAreaWithout(area, 1, 1, area.xsize - ruinw, area.ysize - ruinh, ruinw-1, ruinh-1, new String[]{"wall"});
            if (ruinloc != null) {
                buildComplex(area, ruinloc.x, ruinloc.y, ruinloc.x + ruinw, ruinloc.y + ruinh, "floor", "wall",
                        new String[]{"tree", "sapling", "grass", "water"}, 5, 9+rand(5),
                        0.3f, 3, 4+rand(30), 8+rand(6));
                thinTrees(area, ruinloc.x - 1, ruinloc.y - 1, ruinloc.x + ruinw + 2, ruinloc.y + ruinh + 2);
                System.out.println("digging trapdoor to dungeon");
                UCell doorcell = randomCell(area, "floor", ruinloc.x,ruinloc.y,ruinloc.x+ruinw,ruinloc.y+ruinh);
                if (doorcell != null) {
                    String id = "dungeon-" + Integer.toString(doorcell.x*1000+doorcell.y);
                    URegion region = new URegion(id, "Basement", new ULandscaper[]{new ExampleDungeonScaper()},
                            new String[]{"basement"}, 60, 60, rand(4)+2, "trapdoor", "ladder", "sounds/ultima_dungeon.ogg");
                    linkRegionAt(area, doorcell.x, doorcell.y, "trapdoor", region, 1, "ladder");
                }
                scatterActorsByTags(area, ruinloc.x,ruinloc.y,ruinloc.x+ruinw,ruinloc.y+ruinh,
                        new String[]{"complex"}, 1, 2 + random.nextInt(4));
                scatterThingsByTags(area, ruinloc.x,ruinloc.y,ruinloc.x+ruinw, ruinloc.y+ruinh,
                        new String[]{"complex"}, 1, 3 + rand(8));
            }
        }
        //UCell townloc = findAreaWithout(area, 1,1,area.xsize, area.ysize, 20, 20, new String[]{"water", "wall"});

        scatterThingsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 50);
        scatterActorsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 30);
    }

    public void thinTrees(UArea area, int x1, int y1, int x2, int y2) {
        for (int x=x1;x<=x2;x++) {
            for (int y=y1;y<=y2;y++) {
                if (area.hasTerrainAt(x,y,"tree")) {
                    area.setTerrain(x,y,"sapling");
                }
            }
        }
    }

}
