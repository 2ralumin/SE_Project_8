package com.muleo.soft.util

import com.muleo.soft.entity.Camp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

object Ktor {

    const val URL = "http://172.30.1.59:8080"

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    private const val BY_CAMP = "/camp"
    suspend fun campById(campId: Int): Camp {
        return client.get<Camp>("$URL$BY_CAMP/$campId") {
            //TODO 헤더는 아직 없습니다
            header("Accept", "application/json")
        }
    }
}