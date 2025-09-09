package com.zighang.jobposting.repository

import com.zighang.jobposting.entity.PostingEvaluation
import org.springframework.data.repository.CrudRepository

interface PostingEvaluationRespository : CrudRepository<PostingEvaluation, Long> {
}