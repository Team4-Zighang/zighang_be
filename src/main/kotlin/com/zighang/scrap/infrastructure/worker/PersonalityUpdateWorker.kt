package com.zighang.scrap.infrastructure.worker

import com.zighang.member.entity.Personality
import com.zighang.member.entity.value.personality.CharacterType
import com.zighang.member.entity.value.personality.CompanySize
import com.zighang.member.entity.value.personality.PursuitOfValueType
import com.zighang.member.entity.value.personality.WorkType
import com.zighang.member.repository.PersonalityRepository
import com.zighang.scrap.dto.request.PersonalityUpdateEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PersonalityUpdateWorker(
    private val personalityRepository: PersonalityRepository,
) {

    @Transactional
    @RabbitListener(queues = ["\${mq.personality-update.name}"])
    fun updatePersonality(event: PersonalityUpdateEvent) {
        val companySize = setCompanySize(event.personalityValue.majorValue)
        val workType = setWorkType(event.personalityValue.officeValue)
        val pursuitOfValueType = setPursuitOfValueType(event.personalityValue.feeValue)
        val memberId = event.memberId

        val characterType = getCharacterType(companySize, workType, pursuitOfValueType)

        val existingPersonality = personalityRepository.findByMemberId(event.memberId)

        if (existingPersonality == null) {
            val personality = Personality.create(
                memberId = memberId,
                characterType = characterType,
                companySize = companySize,
                companySizeValue = event.personalityValue.majorValue,
                workType = workType,
                workTypeValue = event.personalityValue.officeValue,
                pursuitOfValueType = pursuitOfValueType,
                pursuitOfValueTypeValue = event.personalityValue.feeValue
            )
            personalityRepository.save(personality)

        } else {
            existingPersonality.update(
                charcterType = characterType,
                companySize = companySize,
                companySizeValue = event.personalityValue.majorValue,
                workType = workType,
                workTypeValue = event.personalityValue.officeValue,
                pursuitOfValueType = pursuitOfValueType,
                pursuitOfValueTypeValue = event.personalityValue.feeValue
            )
        }

    }

    private fun setCompanySize(majorValue: Int): CompanySize {
        return when {
            majorValue > 50 -> CompanySize.MAJOR_COMPANY
            else -> CompanySize.START_UP
        }
    }

    private fun setWorkType(officeValue: Int): WorkType {
        return when {
            officeValue > 50 -> WorkType.OFFICE
            else -> WorkType.REMOTE
        }
    }

    private fun setPursuitOfValueType(feeValue: Int): PursuitOfValueType {
        return when {
            feeValue > 50 -> PursuitOfValueType.WELFARE_FEE
            else -> PursuitOfValueType.PERSONAL_GROWTH
        }
    }

    private fun getCharacterType(
        companySize: CompanySize,
        workType: WorkType,
        pursuitOfValueType: PursuitOfValueType
    ): CharacterType {
        return when {
            companySize == CompanySize.MAJOR_COMPANY &&
                    workType == WorkType.OFFICE &&
                    pursuitOfValueType == PursuitOfValueType.WELFARE_FEE -> CharacterType.DEUMJIK

            companySize == CompanySize.MAJOR_COMPANY &&
                    workType == WorkType.REMOTE &&
                    pursuitOfValueType == PursuitOfValueType.WELFARE_FEE -> CharacterType.DEUMJIK

            companySize == CompanySize.MAJOR_COMPANY &&
                    workType == WorkType.OFFICE &&
                    pursuitOfValueType == PursuitOfValueType.PERSONAL_GROWTH -> CharacterType.SEONGSIL

            companySize == CompanySize.MAJOR_COMPANY &&
                    workType == WorkType.REMOTE &&
                    pursuitOfValueType == PursuitOfValueType.PERSONAL_GROWTH -> CharacterType.MOHEOM

            companySize == CompanySize.START_UP &&
                    workType == WorkType.OFFICE &&
                    pursuitOfValueType == PursuitOfValueType.WELFARE_FEE -> CharacterType.JJIN_DONG

            companySize == CompanySize.START_UP &&
                    workType == WorkType.OFFICE &&
                    pursuitOfValueType == PursuitOfValueType.PERSONAL_GROWTH -> CharacterType.DOJEON

            companySize == CompanySize.START_UP &&
                    workType == WorkType.REMOTE &&
                    pursuitOfValueType == PursuitOfValueType.WELFARE_FEE -> CharacterType.JAYU

            companySize == CompanySize.START_UP &&
                    workType == WorkType.REMOTE &&
                    pursuitOfValueType == PursuitOfValueType.PERSONAL_GROWTH -> CharacterType.JAYU

            else -> CharacterType.SILSOK // 기본값
        }
    }
}