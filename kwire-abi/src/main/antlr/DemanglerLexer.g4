/*
 * Copyright 2025 (C) Karma Krafts & associates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

lexer grammar DemanglerLexer;

fragment DELIMITER: '$';

fragment ARRAY: 'A';
ARRAY_BEGIN: (ARRAY DELIMITER)+ -> pushMode(M_ARRAY);
ARRAY_END: DELIMITER ARRAY;

fragment CLASS: 'C';
CLASS_BEGIN: CLASS DELIMITER -> pushMode(M_CLASS);
CLASS_END: DELIMITER CLASS;

fragment STRUCT: 'S';
STRUCT_BEGIN: STRUCT DELIMITER -> pushMode(M_STRUCT);
STRUCT_END: DELIMITER STRUCT;

fragment TYPE_LIST: 'T';
TYPE_LIST_BEGIN: TYPE_LIST DELIMITER -> pushMode(M_TYPE_LIST);
TYPE_LIST_END: DELIMITER TYPE_LIST;

BUILTIN: [a-z];
NULLABLE_SUFFIX: 'N';

mode M_ARRAY; // [array_mode]

M_ARRAY_END: ARRAY_END -> popMode, type(ARRAY_END);
M_ARRAY_BEGIN: ARRAY_BEGIN -> pushMode(M_ARRAY), type(ARRAY_BEGIN);
M_ARRAY_CLASS_BEGIN: CLASS_BEGIN -> pushMode(M_CLASS), type(CLASS_BEGIN);
M_ARRAY_STRUCT_BEGIN: STRUCT_BEGIN -> pushMode(M_STRUCT), type(STRUCT_BEGIN);
M_ARRAY_TYPE_LIST_BEGIN: TYPE_LIST_BEGIN -> pushMode(M_TYPE_LIST), type(TYPE_LIST_BEGIN);
M_ARRAY_BUILTIN: BUILTIN -> type(BUILTIN);
M_ARRAY_NULLABLE_SUFFIX: NULLABLE_SUFFIX -> type(NULLABLE_SUFFIX);

mode M_CLASS; // (class_mode)

M_CLASS_END: CLASS_END -> popMode, type(CLASS_END);
CLASS_NAME: ~('$')+;

mode M_STRUCT; // (struct_mode)

M_STRUCT_END: STRUCT_END -> popMode, type(STRUCT_END);
STRUCT_NAME: ~('$')+;

mode M_TYPE_LIST; // <type_list_mode>

M_TYPE_LIST_END: TYPE_LIST_END -> popMode, type(TYPE_LIST_END);
M_TYPE_LIST_BEGIN: TYPE_LIST_BEGIN -> pushMode(M_TYPE_LIST), type(TYPE_LIST_BEGIN);
M_TYPE_LIST_ARRAY_BEGIN: ARRAY_BEGIN -> pushMode(M_ARRAY), type(ARRAY_BEGIN);
M_TYPE_LIST_CLASS_BEGIN: CLASS_BEGIN -> pushMode(M_CLASS), type(CLASS_BEGIN);
M_TYPE_LIST_STRUCT_BEGIN: STRUCT_BEGIN -> pushMode(M_STRUCT), type(STRUCT_BEGIN);
M_TYPE_LIST_BUILTIN: BUILTIN -> type(BUILTIN);
M_TYPE_LIST_NULLABLE_SUFFIX: NULLABLE_SUFFIX -> type(NULLABLE_SUFFIX);
WILDCARD: '_';