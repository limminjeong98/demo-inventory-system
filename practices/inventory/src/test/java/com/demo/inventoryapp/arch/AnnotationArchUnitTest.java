package com.demo.inventoryapp.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(
        packages = "com.demo.inventoryapp.inventory",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
class AnnotationArchUnitTest {

    private final Architectures.LayeredArchitecture baseRule = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..");

    // Controller 레이어는 Service 레이어에만 의존한다
    @ArchTest
    final ArchRule test1 = baseRule
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Controller").mayOnlyAccessLayers("Service");

    // Service 레이어는 그 어떤 레이어도 의존하지 않는다
    @ArchTest
    final ArchRule test2 = baseRule
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Repository")
            .whereLayer("Service").mayNotAccessAnyLayer();

    // Repository는 Service 레이어에만 의존한다
    @ArchTest
    final ArchRule test3 = baseRule
            .whereLayer("Repository").mayNotBeAccessedByAnyLayer()
            .whereLayer("Repository").mayOnlyAccessLayers("Service");


}
