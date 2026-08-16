package com.usbbog.orientacionvocacional.data.remote

data class LoginResponseDto(
    val token: String,
    val expiresIn: Long,
    val username: String,
    val role: String,
)

data class UserResponseDto(
    val id: Long?,
    val roleId: Long,
    val programId: Long?,
    val firstName: String,
    val lastName: String,
    val document: String,
    val email: String,
    val username: String?,
    val phone: String?,
    val birthDate: String?,
    val gender: String?,
    val genderOther: String?,
    val departmentId: String?,
    val municipalityId: String?,
    val semester: Int?,
    val active: Boolean,
)

data class DepartmentDto(
    val id: String,
    val name: String,
)

data class MunicipalityDto(
    val id: String,
    val name: String,
    val departmentId: String,
)

data class ProgramDto(
    val id: Long,
    val name: String,
    val areaName: String,
    val url: String?,
)

data class QuestionResponseDto(
    val id: Long,
    val code: String,
    val statement: String,
    val programId: Long,
    val programName: String,
    val areaId: Long,
    val areaName: String,
)

data class TestAnswerDto(
    val questionId: Long,
    val questionCode: String,
    val value: Int,
)

data class TestHistoryDto(
    val id: Long,
    val date: String?,
    val elapsedSeconds: Int?,
    val version: String?,
    val active: Boolean,
)

data class AreaAffinityDto(
    val areaId: Long,
    val areaName: String,
    val affinity: Int,
    val profile: String?,
    val description: String?,
    val logoPath: String?,
    val pachoPath: String?,
)

data class ProgramAffinityDto(
    val programId: Long,
    val programName: String,
    val affinity: Int,
    val description: String?,
    val url: String?,
    val logoPath: String?,
    val areaName: String?,
)

data class TestResultResponseDto(
    val testId: Long,
    val date: String?,
    val predominantAreaId: Long,
    val predominantAreaName: String,
    val profile: String?,
    val areaDescription: String?,
    val areaAffinities: List<AreaAffinityDto>,
    val recommendedPrograms: List<ProgramAffinityDto>,
    val reportName: String,
    val reportUrl: String?,
)

data class RegistrationPayload(
    val programId: Long?,
    val firstName: String,
    val lastName: String,
    val document: String,
    val email: String,
    val username: String,
    val phone: String?,
    val birthDate: String?,
    val gender: String?,
    val genderOther: String?,
    val departmentId: String?,
    val municipalityId: String?,
    val semester: Int?,
    val password: String,
)

data class ProfileUpdatePayload(
    val programId: Long?,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val gender: String?,
    val genderOther: String?,
    val departmentId: String?,
    val municipalityId: String?,
    val semester: Int?,
)
