package test;

import core.AutoWired;
import core.Component;

@Component
public class TestService {

    @AutoWired
    private TestDao testDao;

    public void test() {
        testDao.test();
    }
}
