// Copyright 2024 Maintainers of NUKE.
// Distributed under the MIT License.
// https://github.com/nuke-build/nuke/blob/master/LICENSE

using System;
using FluentAssertions;
using Nuke.Build.Execution.Extensions;
using Nuke.Common;
using Nuke.Common.Execution;
using Xunit;

namespace Nuke.Build.Tests;

public class HandlePlanRequestsAttributeTests
{
    [Fact]
    public void GetGraphDefinition_ComplexSample_Validated()
    {
        var build = new PlanBuild();
        build.ExecutableTargets = ExecutableTargetFactory.CreateAll(build, x => x.Compile);
        var sut = new HandlePlanRequestsAttribute { Build = build };

        var result = sut.GetGraphDefinition();

        result.Should().Be("foo");
    }

    private class PlanBuild : NukeBuild
    {
        public Target PreparationA => _ => _
            .Executes();
        
        public Target PreparationB => _ => _
            .Before(PreparationA)
            .Executes();
        
        public Target Compile => _ => _
            .DependsOn(PreparationA, PreparationB)
            .Executes();

        public Target Test => _ => _
            .DependsOn(Compile)
            .Produces("test-files")
            .Executes();
    }
}
