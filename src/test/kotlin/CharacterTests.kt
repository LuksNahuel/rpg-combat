import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.max

class CharacterTests {
    @Test
    fun `spawns with health at 1000`() {
        val character = Character.spawn()

        assertThat(character.health).isEqualTo(Health.at(1000))
    }

    @Test
    fun `spawns at level 1`() {
        val character = Character.spawn()

        assertThat(character.level).isEqualTo(Level.of(1))
    }

    @Test
    fun `at spawn is alive`() {
        val character = Character.spawn()

        assert(character.isAlive())
    }

    @Test
    fun `attacking a character deals damage to its health`() {
        val attacker = Character.spawn()
        val target = Character.spawn()

        attacker.attack(target, damage = 900)

        assertThat(target.health).isEqualTo(Health.at(100))
    }

    @Test
    fun `dealing more damage than its current health to a character makes it die`() {
        val attacker = Character.spawn()
        val target = Character.spawn()

        attacker.attack(target, damage = 2000)

        assert(target.health.isEmpty())
        assert(!target.isAlive())
    }

    @Test
    fun `healing a character raises its health`() {
        val healer = Character.spawn()
        val target = Character.spawn()
        healer.attack(target, damage = 900)

        healer.heal(target, amount = 900)

        assertThat(target.health).isEqualTo(Health.at(1000))
    }

    @Test
    fun `a dead character cannot be healed`() {
        val healer = Character.spawn()
        val target = Character.spawn()
        target.die()

        assertThrows<InvalidOperationError> {
            healer.heal(target, amount = 900)
        }
    }
}

class InvalidOperationError: Throwable()

class Character private constructor(health: Health, val level: Level) {
    var health = health
        private set

    fun isAlive() = !health.isEmpty()

    fun attack(target: Character, damage: Int) {
        target.health -= damage
    }

    fun heal(target: Character, amount: Int) {
        target.mustNotBeDead()
        target.health += amount
    }

    private fun mustNotBeDead() {
        if (!isAlive()) throw InvalidOperationError()
    }

    fun die() {
        health = Health.empty()
    }

    companion object {
        fun spawn(): Character {
            return Character(Health.at(1000), Level.of(1))
        }
    }
}

data class Health(val points: Int) {
    fun isEmpty() = this == empty()

    operator fun minus(amount: Int): Health {
        return Health(max(0, points - amount))
    }

    operator fun plus(amount: Int): Health {
        return Health(points + amount)
    }

    companion object {
        fun at(value: Int): Health {
            return Health(value)
        }

        fun empty(): Health {
            return Health(0)
        }
    }
}

data class Level(val value: Int) {
    companion object {
        fun of(value: Int): Level {
            return Level(value)
        }
    }
}
