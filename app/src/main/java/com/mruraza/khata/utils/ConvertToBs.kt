package com.mruraza.khata.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
data class BSDate(val day: Int, val month: String, val year: Int)


val referenceAD = LocalDate.of(2023, 1, 1)
val referenceBS = Triple(2079, 9, 17)   // YYYY, MM, DD
val bsCalendar = mapOf(
    1970 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1971 to listOf(31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30, 365),
    1972 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    1973 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 365),
    1974 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1975 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    1976 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    1977 to listOf(30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31, 365),
    1978 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1979 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    1980 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    1981 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    1982 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1983 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    1984 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    1985 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    1986 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1987 to listOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    1988 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    1989 to listOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1990 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1991 to listOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    1992 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 366),
    1993 to listOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1994 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1995 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30, 365),
    1996 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 366),
    1997 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1998 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    1999 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2000 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 365),
    2001 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2002 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2003 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2004 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 365),
    2005 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2006 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2007 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2008 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31, 365),
    2009 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2010 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2011 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2012 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    2013 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2014 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2015 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2016 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    2017 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2018 to listOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2019 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 366),
    2020 to listOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2021 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2022 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30, 365),
    2023 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 366),
    2024 to listOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2025 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2026 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2027 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 365),
    2028 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2029 to listOf(31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30, 365),
    2030 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2031 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 365),
    2032 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2033 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2034 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2035 to listOf(30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31, 365),
    2036 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2037 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2038 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2039 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    2040 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2041 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2042 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2043 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    2044 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2045 to listOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2046 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2047 to listOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2048 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2049 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30, 365),
    2050 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 366),
    2051 to listOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2052 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2053 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30, 365),
    2054 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 366),
    2055 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2056 to listOf(31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30, 365),
    2057 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2058 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 365),
    2059 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2060 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2061 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2062 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31, 365),
    2063 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2064 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2065 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2066 to listOf(30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31, 365),
    2067 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2068 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2069 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2070 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    2071 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2072 to listOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2073 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2074 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    2075 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2076 to listOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2077 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2078 to listOf(31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2079 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2080 to listOf(31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2081 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2082 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2083 to listOf(31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30, 365),
    2084 to listOf(31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30, 365),
    2085 to listOf(31, 32, 31, 32, 30, 31, 30, 30, 29, 30, 30, 30, 366),
    2086 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30, 365),
    2087 to listOf(31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30, 366),
    2088 to listOf(30, 31, 32, 32, 30, 31, 30, 30, 29, 30, 30, 30, 365),
    2089 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30, 365),
    2090 to listOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30, 365),
    2091 to listOf(31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30, 366),
    2092 to listOf(31, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30, 366),
    2093 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31, 365),
    2094 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2095 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365),
    2096 to listOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31, 366),
    2097 to listOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30, 365),
    2098 to listOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30, 365),
    2099 to listOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30, 365)
)

val bsMonths = listOf(
    "बैशाख",
    "जेठ",
    "असार",
    "श्रावण",
    "भदौ",
    "असोज",
    "कार्तिक",
    "मङ्सिर",
    "पौष",
    "माघ",
    "फाल्गुन",
    "चैत"
)

fun nepaliMonthFromNumber(month: String): String {
    val index = month.toIntOrNull()?.minus(1)
        ?: throw IllegalArgumentException("Invalid month: $month")

    return bsMonths.getOrNull(index)
        ?: throw IllegalArgumentException("Month out of range: $month")
}


fun bsToAd(bs: String): LocalDate? {
    val parts = bs.split("-", "/")
    if (parts.size != 3) return null

    val y = parts[0].toInt()
    val m = parts[1].toInt()
    val d = parts[2].toInt()
    return bsToAD(y, m, d)
}

private fun bsToAD(bsYear: Int, bsMonth: Int, bsDay: Int): LocalDate? {
    val referenceAD = LocalDate.of(2023, 1, 1)
    val referenceBS = Triple(2079, 9, 17) // YYYY, MM, DD

    // Check for invalid input
    if (!bsCalendar.containsKey(bsYear)) return null
    if (bsMonth !in 1..12) return null
    val monthDays = bsCalendar[bsYear] ?: return null
    if (bsDay !in 1..monthDays[bsMonth - 1]) return null

    var totalDays = 0

    // Count days from reference BS to target BS
    if (bsYear > referenceBS.first || (bsYear == referenceBS.first && (bsMonth > referenceBS.second || (bsMonth == referenceBS.second && bsDay > referenceBS.third)))) {
        // Forward direction
        var y = referenceBS.first
        var m = referenceBS.second
        var d = referenceBS.third

        while (y < bsYear || m < bsMonth || d < bsDay) {
            d++
            if (d > bsCalendar[y]!![m - 1]) {
                d = 1
                m++
            }
            if (m > 12) {
                m = 1
                y++
            }
            totalDays++
        }
        return referenceAD.plusDays(totalDays.toLong())
    } else if (bsYear < referenceBS.first || (bsYear == referenceBS.first && (bsMonth < referenceBS.second || (bsMonth == referenceBS.second && bsDay < referenceBS.third)))) {
        // Backward direction
        var y = referenceBS.first
        var m = referenceBS.second
        var d = referenceBS.third

        while (y > bsYear || m > bsMonth || d > bsDay) {
            d--
            if (d < 1) {
                m--
                if (m < 1) {
                    m = 12
                    y--
                }
                d = bsCalendar[y]!![m - 1]
            }
            totalDays--
        }
        return referenceAD.plusDays(totalDays.toLong())
    } else {
        // Exact reference date
        return referenceAD
    }
}

fun bsMonthIndex(month: String): Int {
    return bsMonths.indexOf(month) // 0-based
}

fun BSDate.toComparable(): Int {
    val m = bsMonthIndex(month) + 1
    return year * 10_000 + m * 100 + day
}

fun convertToBS(timestamp: Long): BSDate {
    val adDate = java.time.Instant.ofEpochMilli(timestamp)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()

    val daysDiff = ChronoUnit.DAYS.between(referenceAD, adDate).toInt()

    // Step 3: Start from reference BS date
    var bsYear = referenceBS.first
    var bsMonth = referenceBS.second
    var bsDay = referenceBS.third
    var remainingDays = daysDiff

    // Logic to move forward (remainingDays > 0)
    if (remainingDays > 0) {
        while (remainingDays > 0) {
            val daysInMonth = bsCalendar[bsYear]!![bsMonth - 1]

            // Check if remaining days can fit in the rest of the month
            val daysLeftInMonth = daysInMonth - bsDay + 1
            if (remainingDays < daysLeftInMonth) {
                bsDay += remainingDays
                remainingDays = 0
            } else {
                remainingDays -= daysLeftInMonth
                bsDay = 1
                bsMonth++

                // Move to the next year if month overflows
                if (bsMonth > 12) {
                    bsMonth = 1
                    bsYear++
                }
            }
        }
    }
    // Logic to move backward (remainingDays < 0)
    else if (remainingDays < 0) {
        while (remainingDays < 0) {
            // Check if current day can absorb the negative days
            if (bsDay > kotlin.math.abs(remainingDays)) {
                bsDay += remainingDays // effectively bsDay - abs(remainingDays)
                remainingDays = 0
            } else {
                remainingDays += bsDay // remainingDays is now less negative
                bsDay = 0
                bsMonth--

                // Move to the previous year if month underflows
                if (bsMonth < 1) {
                    bsMonth = 12
                    bsYear--
                }

                // Set the day to the last day of the new month
                bsDay = bsCalendar[bsYear]!![bsMonth - 1]
            }
        }
    }


    return BSDate(
        day = bsDay,
        month = bsMonths[bsMonth - 1],
        year = bsYear
    )
}
