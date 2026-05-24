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

        var n_currents = 0

        while (n_currents < 5) {
            currents.add(null)
            n_currents += 1
        }
    }

    fun reset() {
        for (i in 0..<currents.get_size()) {
            val monster = get_raw(i)
            monster.set_none()
        }

        ranks = 0

        for (rank in 0..<currents.get_size()) {
            currents.set(rank, null)
        }
    }

    fun get_raw(i: Int): Monster = raws.get(i)

    fun get(rank: Int): Monster? = currents.get(rank)

    fun set(
        rank: Int,
        monster: Monster,
    ) {
        currents.set(rank, monster)
    }

    fun is_end(): Boolean {
        var result = true

        for (rank in 0..<ranks) {
            val monster = get(rank)!!

            if (!monster.is_end()) {
                result = false
            }
        }

        return result
    }

    fun can_start_next(
        level: Int,
        device: Device,
    ): Boolean {
        var result = true

        val threshold_min = device.clock.get_n_animations_min()
        var n_touchables = 0
        val rank_max = get_rank_max(level)

        for (rank in 0..<ranks) {
            val monster = get(rank)!!
            val can_come = monster.timer.counts < threshold_min

            if ((monster.is_come() || monster.is_fall()) && can_come) {
                result = false
            } else if (monster.is_touchable()) {
                n_touchables += 1

                if (n_touchables >= rank_max) {
                    result = false
                }
            }
        }

        return result
    }

    fun get_rank_max(level: Int): Int = level + 1

    fun get_zone_next(board: Board): Int {
        var result: Int = Layout.NONE

        val size_zones = board.zones.get_size()
        val zone_start = Math.rand(size_zones)

        for (i in zone_start..<zone_start + size_zones) {
            val zone = board.zones.get(i % size_zones)
            var is_forbidden = false

            for (rank in 0..<ranks) {
                val monster = get(rank)!!

                if (!monster.is_end() && monster.zone == zone) {
                    is_forbidden = true
                }
            }

            if (!is_forbidden) {
                result = zone
            }
        }

        return result
    }

    fun get_next(level: Int): Monster {
        var result: Monster = get_raw(level)

        val size_raws = raws.get_size()
        val i_start = Math.rand(size_raws)
        val n_friends = get_n_friends()
        val rank_max = get_rank_max(level)
        val friend_forbidden = n_friends == 1 && rank_max == 2

        for (i in i_start..<i_start + size_raws) {
            val monster = get_raw(i % size_raws)
            var is_forbidden = false

            for (rank in 0..<ranks) {
                val is_same = monster == get(rank)

                if (is_same || (!monster.is_enemy() && friend_forbidden)) {
                    is_forbidden = true
                }
            }

            if (!is_forbidden) {
                result = monster
            }
        }

        return result
    }

    fun get_n_friends(): Int {
        var result = 0

        for (rank in 0..<ranks) {
            val monster = get(rank)!!

            if (!monster.is_enemy() && monster.is_touchable()) {
                result += 1
            }
        }

        return result
    }

    fun is_attack(zone: Int): Boolean {
        var result = false

        for (rank in 0..<ranks) {
            val monster = get(rank)!!
            val is_enemy = monster.is_enemy()

            if (is_enemy && monster.is_wait() && monster.zone == zone) {
                result = true
            }
        }

        return result
    }

    fun draw_sprites(
        northern_zone: Boolean,
        board: Board,
        device: Device,
    ) {
        for (rank in 0..<ranks) {
            val monster = get(rank)!!
            val can_draw = board.can_draw_zone(monster.zone, northern_zone)

            if (!monster.is_end() && can_draw) {
                monster.draw(board, device)
            }
        }
    }

    fun draw_cherry(
        board: Board,
        device: Device,
    ) {
        for (rank in 0..<ranks) {
            val monster = get(rank)!!

            if (monster.is_fall()) {
                val ratio = monster.timer.get_ratio()
                val zone = monster.zone
                cherry.draw(ratio, zone, board, device)
            }
        }
    }
}
