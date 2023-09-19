package com.naozumi.izinboss.model.datamodel

data class Company (
    val id: String? = null,
    val name: String? = null,
    val industrySector: IndustrySector? = null
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