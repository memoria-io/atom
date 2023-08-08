package io.memoria.atom.testsuite.eventsourcing.banking.state;

import io.memoria.atom.eventsourcing.State;

public sealed interface Account extends State permits OpenAccount, ClosedAccount {}
