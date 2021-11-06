package com.example.newevent2;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class NotificationID {
        private final static AtomicInteger c = new AtomicInteger(0);
        public static int getID() {
            return c.incrementAndGet();
        }
}
