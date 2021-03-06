/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package root.savaris.githubrepositories.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepositoryLocalItemEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int?=null,
    val name: String?=null,
    val fullName: String?=null,
    val isPrivate: Boolean?=null,
    val ownerId: Int?=null,
    val ownerLogin: String?=null,
    val ownerAvatar: String?=null,
    val description: String?=null,
    val stargazersCount: Int?=null,
    val watchersCount: Int?=null,
    val subscribers_count: Int?=null,
    val forks_count: Int?=null
)