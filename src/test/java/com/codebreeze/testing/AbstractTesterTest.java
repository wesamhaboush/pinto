package com.codebreeze.testing;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractTesterTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private enum TestType {}

    @Test
    public void getFactoryForClass_should_throw_illegal_argument_exception_if_enum_has_no_members()
    throws Exception
    {
        expectedException.expect(IllegalArgumentException.class);
        new AbstractTester().getFactoryForClass(TestType.class);
    }

    @Test
    public void can_convert_from_string()
    {
        assertThat(AbstractTester.ClassType.valueOf("NON_FINAL")).isNotNull();
    }
}
