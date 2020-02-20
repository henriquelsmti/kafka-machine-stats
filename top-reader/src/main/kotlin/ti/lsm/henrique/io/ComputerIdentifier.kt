package ti.lsm.henrique.io

import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.hardware.ComputerSystem
import oshi.hardware.HardwareAbstractionLayer
import oshi.software.os.OperatingSystem
import javax.inject.Singleton


@Singleton
class ComputerIdentifier {

    val id = generateKey()

    fun generateKey(): String {
        val systemInfo = SystemInfo()
        val operatingSystem: OperatingSystem = systemInfo.operatingSystem
        val hardwareAbstractionLayer: HardwareAbstractionLayer = systemInfo.hardware
        val centralProcessor: CentralProcessor = hardwareAbstractionLayer.processor
        val computerSystem: ComputerSystem = hardwareAbstractionLayer.computerSystem
        val vendor: String = operatingSystem.manufacturer
        val processorSerialNumber: String = computerSystem.serialNumber
        val processorIdentifier: String = centralProcessor.identifier
        val processors: Int = centralProcessor.logicalProcessorCount
        val delimiter = "#"
        return vendor +
                delimiter +
                processorSerialNumber +
                delimiter +
                computerSystem.baseboard +
                delimiter +
                processorIdentifier +
                delimiter +
                processors
    }
}