package me.ztowne13.customcrates.crates

class Crate(var name: String, var isEnabled: Boolean, var loaded: Boolean, var multiCrate: Boolean, var configuration: CrateConfiguration, var instances: CrateInstances ) {

    override fun toString(): String {
        return "Crate(name=$name, isEnabled=$isEnabled, loaded=$loaded, multiCrate=$multiCrate, configuration=$configuration, instances=$instances)"
    }

    class CrateBuilder internal constructor() {
        private lateinit var name: String
        private var isEnabled: Boolean = false
        private var loaded: Boolean = false
        private var multiCrate: Boolean = false
        private lateinit var configuration: CrateConfiguration
        private lateinit var instances: CrateInstances

        fun name(name: String): CrateBuilder {
            this.name = name
            return this
        }

        fun isEnabled(isEnabled: Boolean): CrateBuilder {
            this.isEnabled = isEnabled
            return this
        }

        fun loaded(loaded: Boolean): CrateBuilder {
            this.loaded = loaded
            return this
        }

        fun multiCrate(multiCrate: Boolean): CrateBuilder {
            this.multiCrate = multiCrate
            return this
        }

        fun configuration(configuration: CrateConfiguration): CrateBuilder {
            this.configuration = configuration
            return this
        }

        fun instances(instances: CrateInstances): CrateBuilder {
            this.instances = instances
            return this
        }

        override fun toString(): String {
            return "Crate(name=$name, isEnabled=$isEnabled, loaded=$loaded, multiCrate=$multiCrate, configuration=$configuration, instances=$instances)"
        }
    }

    companion object {
        fun builder(): CrateBuilder {
            return CrateBuilder()
        }
    }
}