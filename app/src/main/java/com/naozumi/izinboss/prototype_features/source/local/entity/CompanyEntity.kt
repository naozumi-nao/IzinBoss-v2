package com.naozumi.izinboss.prototype_features.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CompanyEntity (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val industrySector: IndustrySector
) {
    enum class IndustrySector {
        INFORMATION_TECHNOLOGY,
        HEALTHCARE_AND_PHARMACEUTICALS,
        FINANCE_AND_BANKING,
        RETAIL,
        MANUFACTURING,
        TELECOMMUNICATIONS,
        ENERGY,
        CONSTRUCTION_AND_REAL_ESTATE,
        TRANSPORTATION_AND_LOGISTICS,
        HOSPITALITY_AND_TOURISM,
        EDUCATION,
        ENTERTAINMENT,
        AUTOMOTIVE,
        AGRICULTURE,
        MEDIA_AND_ADVERTISING,
        CONSULTING,
        LEGAL,
        GOVERNMENT,
        NONPROFIT,
        OTHER
    }
}