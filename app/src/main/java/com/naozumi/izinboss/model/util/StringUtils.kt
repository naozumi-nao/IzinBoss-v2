package com.naozumi.izinboss.model.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.naozumi.izinboss.model.datamodel.Company

object StringUtils {
    fun copyTextToClipboard(context: Context, textToCopy: CharSequence) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    fun capitalizeWordsExceptAnd(input: String): String {
        val words = input.split("_")
        val capitalizedWords = words.map { word ->
            if (word.lowercase() != "and") {
                word.replaceFirstChar { it.uppercase() }
            } else {
                word
            }
        }
        return capitalizedWords.joinToString(" ")
    }

    val industrySectorMap = mapOf(
        "Information Technology" to Company.IndustrySector.INFORMATION_TECHNOLOGY,
        "Healthcare and Pharmaceuticals" to Company.IndustrySector.HEALTHCARE_AND_PHARMACEUTICALS,
        "Finance and Banking" to Company.IndustrySector.FINANCE_AND_BANKING,
        "Retail" to Company.IndustrySector.RETAIL,
        "Manufacturing" to Company.IndustrySector.MANUFACTURING,
        "Telecommunications" to Company.IndustrySector.TELECOMMUNICATIONS,
        "Energy" to Company.IndustrySector.ENERGY,
        "Construction" to Company.IndustrySector.CONSTRUCTION_AND_REAL_ESTATE,
        "Transportation and Logistics" to Company.IndustrySector.TRANSPORTATION_AND_LOGISTICS,
        "Hospitality and Tourism" to Company.IndustrySector.HOSPITALITY_AND_TOURISM,
        "Education" to Company.IndustrySector.EDUCATION,
        "Entertainment" to Company.IndustrySector.ENTERTAINMENT,
        "Automotive" to Company.IndustrySector.AUTOMOTIVE,
        "Agriculture" to Company.IndustrySector.AGRICULTURE,
        "Media and Advertising" to Company.IndustrySector.MEDIA_AND_ADVERTISING,
        "Consulting" to Company.IndustrySector.CONSULTING,
        "Legal" to Company.IndustrySector.LEGAL,
        "Government" to Company.IndustrySector.GOVERNMENT,
        "Nonprofit" to Company.IndustrySector.NONPROFIT,
        "Other" to Company.IndustrySector.OTHER
    )
}