FILE(REMOVE_RECURSE
  "CMakeFiles/gradle-clean"
)

# Per-language clean rules from dependency scanning.
FOREACH(lang)
  INCLUDE(CMakeFiles/gradle-clean.dir/cmake_clean_${lang}.cmake OPTIONAL)
ENDFOREACH(lang)
