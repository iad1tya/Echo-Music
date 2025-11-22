package iad1tya.echo.music.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import iad1tya.echo.music.R

enum class CryptoType(val displayName: String, val address: String, val icon: Int) {
    BITCOIN("Bitcoin", "bc1qcvyr7eekha8uytmffcvgzf4h7xy7shqzke35fy", R.drawable.currency_bitcoin),
    ETHEREUM("Ethereum", "0x51bc91022E2dCef9974D5db2A0e22d57B360e700", R.drawable.currency_ethereum),
    SOLANA("Solana", "9wjca3EQnEiqzqgy7N5iqS1JGXJiknMQv6zHgL96t94S", R.drawable.currency_solana)
}

@Composable
fun CryptoSelectionDialog(
    onDismiss: () -> Unit,
    onCryptoSelected: (CryptoType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp)
            ) {
                // Header
                Text(
                    text = "Select a cryptocurrency",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(28.dp))

                // Crypto options as cards
                CryptoType.values().forEachIndexed { index, crypto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCryptoSelected(crypto) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        when (crypto) {
                                            CryptoType.BITCOIN -> Color(0xFFF7931A).copy(alpha = 0.15f)
                                            CryptoType.ETHEREUM -> Color(0xFF627EEA).copy(alpha = 0.15f)
                                            CryptoType.SOLANA -> Color(0xFF14F195).copy(alpha = 0.15f)
                                        },
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (crypto == CryptoType.BITCOIN) {
                                    Image(
                                        painter = painterResource(crypto.icon),
                                        contentDescription = crypto.displayName,
                                        modifier = Modifier.size(32.dp),
                                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                            Color(0xFFF7931A)
                                        )
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(crypto.icon),
                                        contentDescription = crypto.displayName,
                                        modifier = Modifier.size(32.dp),
                                        tint = when (crypto) {
                                            CryptoType.ETHEREUM -> Color(0xFF627EEA)
                                            CryptoType.SOLANA -> Color(0xFF14F195)
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                            }

                            Spacer(Modifier.width(18.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = crypto.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (crypto) {
                                        CryptoType.BITCOIN -> "BTC Network"
                                        CryptoType.ETHEREUM -> "ETH Network"
                                        CryptoType.SOLANA -> "SOL Network"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                painter = painterResource(R.drawable.arrow_forward),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (index < CryptoType.values().size - 1) {
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Cancel button
                androidx.compose.material3.FilledTonalButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoDetailsDialog(
    cryptoType: CryptoType,
    context: Context,
    onDismiss: () -> Unit
) {
    val qrBitmap = remember(cryptoType) {
        generateQRCode(cryptoType.address)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (cryptoType == CryptoType.BITCOIN) {
                        Image(
                            painter = painterResource(cryptoType.icon),
                            contentDescription = cryptoType.displayName,
                            modifier = Modifier.size(32.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                        )
                    } else {
                        Icon(
                            painter = painterResource(cryptoType.icon),
                            contentDescription = cryptoType.displayName,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = cryptoType.displayName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.height(24.dp))

                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Wallet Address",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cryptoType.address,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Crypto Address", cryptoType.address)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.content_copy),
                            contentDescription = "Copy",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Copy")
                    }

                    androidx.compose.material3.Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

fun generateQRCode(text: String): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        val size = 512
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
        val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.RGB_565)
        
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
