package top.flvr.ssh_hid_client.report_senders

import kossh.impl.SSH
import kossh.impl.SSHOptions
import kossh.impl.SSHShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException

abstract class ReportSender() {
    private val reportsChannel = Channel<ByteArray>(Channel.UNLIMITED) {
        Timber.wtf("A channel with an unlimited buffer shouldn't be failing to receive elements")
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun start(host: String, user: String, password: String, onSuccess: () -> Unit, onException: (e: IOException) -> Unit) =
        withContext(Dispatchers.IO) {
            val shell = SSH(
                SSHOptions(host, user, password)
            ).newShell()
            shell.disableHistory()

            for (report in reportsChannel) {
                try {
                    Timber.d("REPORT HEX (len = %d): %s", report.size, report.toHexString())
                    sendReport(report, shell)
                    onSuccess()
                } catch (e: IOException) {
                    Timber.d(e)

                    // TODO: map exception to a sealed error type and pass that to lambda?
                    onException(e)
                }
            }
        }

    // IMPORTANT: Implement this when extending this class. Parameter list can be any number of bytes.
    // public void addReport(byte foo, byte bar, byte baz) {
    //     super.addReportToChannel(new byte[]{foo, bar, baz});
    // }
    //
    // Of course, make sure the argument list matches what the character device is expecting.
    protected fun addReportToChannel(report: ByteArray) {
        // This should always succeed since the Channel's buffer is unlimited
        reportsChannel.trySend(report)
    }

    open fun sendReport(report: ByteArray, shell: SSHShell) {
        writeBytes(report, shell)
    }

    // Writes HID report to character device
    @OptIn(ExperimentalStdlibApi::class)
    @Throws(IOException::class, FileNotFoundException::class)
    fun writeBytes(report: ByteArray, shell: SSHShell) {
        val keycode = report.toHexString(
            HexFormat {
                bytes {
                    bytesPerLine = Int.MAX_VALUE
                    bytesPerGroup = Int.MAX_VALUE
                    groupSeparator = ""
                    byteSeparator = ""
                    bytePrefix = "\\x"
                    byteSuffix = ""
                }
            }
        )
        val cmd = "echo -ne \"${keycode}\" | sudo tee -a /dev/hidg0"
//        Timber.i(cmd)
        shell.execute(cmd)
    }
}
