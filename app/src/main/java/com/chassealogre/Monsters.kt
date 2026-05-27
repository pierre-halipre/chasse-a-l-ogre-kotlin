package com.chassealogre

class Monsters(
    board: Board,
    device: Device,
) {
    val raws: Array<Monster> = Array<Monster>()

    var ranks: Int = 0
    val currents: Array<Monster?> = Array<Monster?>()

    val cherry: Cherry = Cherry(board, device)

    init {
        val graphic = device.graphic
        raws.add(Enemy(13.0 / 7, 17.0 / 7, board, graphic.zombie, device))
        raws.add(Enemy(12.0 / 7, 18.0 / 7, board, graphic.vampire, device))
        raws.add(Friend(9.0 / 7, 15.0 / 7, board, graphic.deer, device))
        raws.add(Enemy(11.0 / 7, 19.0 / 7, board, graphic.skeleton, device))
        raws.add(Enemy(10.0 / 7, 20.0 / 7, board, graphic.ghost, device))
        raws.add(Friend(8.0 / 7, 16.0 / 7, board, graphic.rabbit, device))

        var nCurrents = 0

        while (nCurrents < 5) {
            currents.add(null)
            nCurrents += 1
        }
    }

    fun reset() {
        for (i in 0..<currents.getSize()) {
            val monster = getRaw(i)
            monster.setNone()
        }

        ranks = 0

        for (rank in 0..<currents.getSize()) {
            currents.set(rank, null)
        }
    }

    fun getRaw(i: Int): Monster = raws.get(i)

    fun get(rank: Int): Monster? = currents.get(rank)

    fun set(
        rank: Int,
        monster: Monster,
    ) {
        currents.set(rank, monster)
    }

    fun isEnd(): Boolean {
        var result = true

        for (rank in 0..<ranks) {
            val monster = get(rank)!!

            if (!monster.isEnd()) {
                result = false
            }
        }

        return result
    }

    fun canStartNext(
        level: Int,
        device: Device,
    ): Boolean {
        var result = true

        val thresholdMin = device.clock.getNAnimationsMin()
        var nTouchables = 0
        val rankMax = getRankMax(level)

        for (rank in 0..<ranks) {
            val monster = get(rank)!!
            val canCome = monster.timer.counts < thresholdMin

            if ((monster.isCome() || monster.isFall()) && canCome) {
                result = false
            } else if (monster.isTouchable()) {
                nTouchables += 1

                if (nTouchables >= rankMax) {
                    result = false
                }
            }
        }

        return result
    }

    fun getRankMax(level: Int): Int = level + 1

    fun getZoneNext(board: Board): Int {
        var result: Int = Layout.NONE

        val sizeZones = board.zones.getSize()
        val zoneStart = Math.rand(sizeZones)

        for (i in zoneStart..<zoneStart + sizeZones) {
            val zone = board.zones.get(i % sizeZones)
            var isForbidden = false

            for (rank in 0..<ranks) {
                val monster = get(rank)!!

                if (!monster.isEnd() && monster.zone == zone) {
                    isForbidden = true
                }
            }

            if (!isForbidden) {
                result = zone
            }
        }

        return result
    }

    fun getNext(level: Int): Monster {
        var result: Monster = getRaw(level)

        val sizeRaws = raws.getSize()
        val iStart = Math.rand(sizeRaws)
        val nFriends = getNFriends()
        val rankMax = getRankMax(level)
        val friendForbidden = nFriends == 1 && rankMax == 2

        for (i in iStart..<iStart + sizeRaws) {
            val monster = getRaw(i % sizeRaws)
            var isForbidden = false

            for (rank in 0..<ranks) {
                val isSame = monster == get(rank)

                if (isSame || (!monster.isEnemy() && friendForbidden)) {
                    isForbidden = true
                }
            }

            if (!isForbidden) {
                result = monster
            }
        }

        return result
    }

    fun getNFriends(): Int {
        var result = 0

        for (rank in 0..<ranks) {
            val monster = get(rank)!!

            if (!monster.isEnemy() && monster.isTouchable()) {
                result += 1
            }
        }

        return result
    }

    fun isAttack(zone: Int): Boolean {
        var result = false

        for (rank in 0..<ranks) {
            val monster = get(rank)!!
            val isEnemy = monster.isEnemy()

            if (isEnemy && monster.isWait() && monster.zone == zone) {
                result = true
            }
        }

        return result
    }

    fun drawSprites(
        northernZone: Boolean,
        board: Board,
        device: Device,
    ) {
        for (rank in 0..<ranks) {
            val monster = get(rank)!!
            val canDraw = board.canDrawZone(monster.zone, northernZone)

            if (!monster.isEnd() && canDraw) {
                monster.draw(board, device)
            }
        }
    }

    fun drawCherry(
        board: Board,
        device: Device,
    ) {
        for (rank in 0..<ranks) {
            val monster = get(rank)!!

            if (monster.isFall()) {
                val ratio = monster.timer.getRatio()
                val zone = monster.zone
                cherry.draw(ratio, zone, board, device)
            }
        }
    }
}
