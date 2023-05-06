// ------------------------------------------------------------------------------
// <auto-generated>
//
//     This code was generated.
//
//     - To turn off auto-generation set:
//
//         [TeamCity (AutoGenerate = false)]
//
//     - To trigger manual generation invoke:
//
//         nuke --generate-configuration TeamCity --host TeamCity
//
// </auto-generated>
// ------------------------------------------------------------------------------

import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildFeatures.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.*
import jetbrains.buildServer.configs.kotlin.v2018_1.triggers.*
import jetbrains.buildServer.configs.kotlin.v2018_1.vcs.*

version = "2021.2"

project {
    buildType(Pack)
    buildType(Test_P1T2)
    buildType(Test_P2T2)
    buildType(Test)
    buildType(ReportDuplicates)
    buildType(ReportIssues)
    buildType(ReportCoverage)

    buildTypesOrder = arrayListOf(Pack, Test_P1T2, Test_P2T2, Test, ReportDuplicates, ReportIssues, ReportCoverage)

    params {
        checkbox (
            "env.AutoStash",
            label = "AutoStash",
            value = "True",
            checked = "True",
            unchecked = "False",
            display = ParameterDisplay.NORMAL)
        select (
            "env.Configuration",
            label = "Configuration",
            value = "Release",
            options = listOf("Debug" to "Debug", "Release" to "Release"),
            display = ParameterDisplay.NORMAL)
        checkbox (
            "env.IgnoreFailedSources",
            label = "IgnoreFailedSources",
            description = "Ignore unreachable sources during Restore",
            value = "False",
            checked = "True",
            unchecked = "False",
            display = ParameterDisplay.NORMAL)
        checkbox (
            "env.Major",
            label = "Major",
            value = "False",
            checked = "True",
            unchecked = "False",
            display = ParameterDisplay.NORMAL)
        text (
            "env.SignPathOrganizationId",
            label = "SignPathOrganizationId",
            value = "0fdaf334-6910-41f4-83d2-e58e4cccb087",
            allowEmpty = true,
            display = ParameterDisplay.NORMAL)
        text (
            "env.SignPathPolicySlug",
            label = "SignPathPolicySlug",
            value = "release-signing",
            allowEmpty = true,
            display = ParameterDisplay.NORMAL)
        text (
            "env.SignPathProjectSlug",
            label = "SignPathProjectSlug",
            value = "nuke",
            allowEmpty = true,
            display = ParameterDisplay.NORMAL)
        text (
            "env.TestDegreeOfParallelism",
            label = "TestDegreeOfParallelism",
            value = "1",
            allowEmpty = true,
            display = ParameterDisplay.NORMAL)
        checkbox (
            "env.UseHttps",
            label = "UseHttps",
            value = "False",
            checked = "True",
            unchecked = "False",
            display = ParameterDisplay.NORMAL)
        select (
            "env.Verbosity",
            label = "Verbosity",
            description = "Logging verbosity during build execution. Default is 'Normal'.",
            value = "Normal",
            options = listOf("Minimal" to "Minimal", "Normal" to "Normal", "Quiet" to "Quiet", "Verbose" to "Verbose"),
            display = ParameterDisplay.NORMAL)
        text(
            "teamcity.runner.commandline.stdstreams.encoding",
            "UTF-8",
            display = ParameterDisplay.HIDDEN)
        text(
            "teamcity.git.fetchAllHeads",
            "true",
            display = ParameterDisplay.HIDDEN)
    }
}
object Pack : BuildType({
    name = "📦 Pack"
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    artifactRules = "output/packages/*.nupkg => output/packages"
    steps {
        exec {
            path = "build.cmd"
            arguments = "Restore Compile DownloadLicenses Pack --skip"
            conditions { contains("teamcity.agent.jvm.os.name", "Windows") }
        }
        exec {
            path = "build.sh"
            arguments = "Restore Compile DownloadLicenses Pack --skip"
            conditions { doesNotContain("teamcity.agent.jvm.os.name", "Windows") }
        }
    }
    params {
        text(
            "teamcity.ui.runButton.caption",
            "Pack",
            display = ParameterDisplay.HIDDEN)
    }
    triggers {
        vcs {
            triggerRules = "+:**"
        }
    }
})
object Test_P1T2 : BuildType({
    name = "🚦 Test 🧩 1/2"
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    artifactRules = """
        output/test-results/*.trx => output/test-results
        output/test-results/*.xml => output/test-results
    """.trimIndent()
    steps {
        exec {
            path = "build.cmd"
            arguments = "Restore Compile Test --skip --partition 1/2"
            conditions { contains("teamcity.agent.jvm.os.name", "Windows") }
        }
        exec {
            path = "build.sh"
            arguments = "Restore Compile Test --skip --partition 1/2"
            conditions { doesNotContain("teamcity.agent.jvm.os.name", "Windows") }
        }
    }
})
object Test_P2T2 : BuildType({
    name = "🚦 Test 🧩 2/2"
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    artifactRules = """
        output/test-results/*.trx => output/test-results
        output/test-results/*.xml => output/test-results
    """.trimIndent()
    steps {
        exec {
            path = "build.cmd"
            arguments = "Restore Compile Test --skip --partition 2/2"
            conditions { contains("teamcity.agent.jvm.os.name", "Windows") }
        }
        exec {
            path = "build.sh"
            arguments = "Restore Compile Test --skip --partition 2/2"
            conditions { doesNotContain("teamcity.agent.jvm.os.name", "Windows") }
        }
    }
})
object Test : BuildType({
    name = "🚦 Test"
    type = Type.COMPOSITE
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
        showDependenciesChanges = true
    }
    artifactRules = "**/*"
    params {
        text(
            "teamcity.ui.runButton.caption",
            "Test",
            display = ParameterDisplay.HIDDEN)
    }
    triggers {
        vcs {
            triggerRules = "+:**"
        }
    }
    dependencies {
        snapshot(Test_P1T2) {
            onDependencyFailure = FailureAction.ADD_PROBLEM
            onDependencyCancel = FailureAction.CANCEL
        }
        snapshot(Test_P2T2) {
            onDependencyFailure = FailureAction.ADD_PROBLEM
            onDependencyCancel = FailureAction.CANCEL
        }
        artifacts(Test_P1T2) {
            artifactRules = "**/*"
        }
        artifacts(Test_P2T2) {
            artifactRules = "**/*"
        }
    }
})
object ReportDuplicates : BuildType({
    name = "🎭 ReportDuplicates"
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    steps {
        exec {
            path = "build.cmd"
            arguments = "ReportDuplicates --skip"
            conditions { contains("teamcity.agent.jvm.os.name", "Windows") }
        }
        exec {
            path = "build.sh"
            arguments = "ReportDuplicates --skip"
            conditions { doesNotContain("teamcity.agent.jvm.os.name", "Windows") }
        }
    }
    params {
        text(
            "teamcity.ui.runButton.caption",
            "Report Duplicates",
            display = ParameterDisplay.HIDDEN)
    }
    triggers {
        vcs {
            triggerRules = "+:**"
        }
    }
})
object ReportIssues : BuildType({
    name = "💣 ReportIssues"
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    steps {
        exec {
            path = "build.cmd"
            arguments = "Restore ReportIssues --skip"
            conditions { contains("teamcity.agent.jvm.os.name", "Windows") }
        }
        exec {
            path = "build.sh"
            arguments = "Restore ReportIssues --skip"
            conditions { doesNotContain("teamcity.agent.jvm.os.name", "Windows") }
        }
    }
    params {
        text(
            "teamcity.ui.runButton.caption",
            "Report Issues",
            display = ParameterDisplay.HIDDEN)
    }
    triggers {
        vcs {
            triggerRules = "+:**"
        }
    }
})
object ReportCoverage : BuildType({
    name = "📊 ReportCoverage"
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    artifactRules = "output/reports/coverage-report.zip => output/reports"
    steps {
        exec {
            path = "build.cmd"
            arguments = "ReportCoverage --skip"
            conditions { contains("teamcity.agent.jvm.os.name", "Windows") }
        }
        exec {
            path = "build.sh"
            arguments = "ReportCoverage --skip"
            conditions { doesNotContain("teamcity.agent.jvm.os.name", "Windows") }
        }
    }
    params {
        text(
            "teamcity.ui.runButton.caption",
            "Report Coverage",
            display = ParameterDisplay.HIDDEN)
    }
    triggers {
        vcs {
            triggerRules = "+:**"
        }
    }
    dependencies {
        snapshot(Test) {
            onDependencyFailure = FailureAction.FAIL_TO_START
            onDependencyCancel = FailureAction.CANCEL
        }
        artifacts(Test) {
            artifactRules = """
                output/test-results/*.trx => output/test-results
                output/test-results/*.xml => output/test-results
            """.trimIndent()
        }
    }
})
