# Cafe order dispatch

## Problem and independent variation

A cafe prepares dine-in and takeaway orders at its kitchen, bar and bakery.
Dine-in tickets carry a table number and serving instructions. Takeaway tickets
carry packing instructions and no table. Separately, each station may use a kitchen
screen, a modern text printer or a legacy byte-oriented printer. Both order types
must work with every output. Encoding each pair as a subclass would duplicate order
rules across six combinations; adding a new output would multiply that duplication.

## Bridge and Adapter in one request

CafeOrder holds an OrderOutput. DineInOrder and TakeawayOrder build the appropriate
ticket and delegate delivery. KitchenScreen and ReceiptPrinter implement OrderOutput
directly. LegacyPrinterAdapter is the third implementor; it wraps LegacyPrinter.
The abstraction hierarchy never imports legacy classes or status constants.

Bridge alone separates ticket rules from delivery but cannot speak the old printer's
protocol. Adapter alone makes that printer usable but does not separate the two
independently varying order policies from output implementations. In this design,
one order passes through the bridge and, when selected, through the adapter.

## Genuine incompatibility and failure translation

LegacyPrinter is a standalone simulated legacy component, not a claimed vendor SDK.
Its source has no dependency on OrderOutput and is not altered to fit that interface.
The target send(String) returns a delivery receipt and throws OutputException.
The native printBytes(byte[], int) requires UTF-8 encoded data plus a copy count and
returns integer statuses. The adapter encodes text, supplies exactly one copy,
and turns successful status 0 into a receipt. Status -1 becomes PAPER_OUT, -2 OFFLINE,
and -3 INVALID_ORDER. Any unknown status or unexpected runtime exception becomes
DEVICE_FAILURE. No legacy type, raw status, cause or diagnostic message leaks out.

The legacy device supports 1..256 bytes and 1..3 copies. The common contract permits
backend-specific input limits reported as INVALID_ORDER. The adapter does not truncate
orders or silently change them. Only successful sends advance its receipt counter.

## Complexity module and Open/Closed Principle

Chosen module: **dynamic implementor selection**. OrderService selects an output
using the incoming preparation station. Main registers available objects at startup;
it does not hard-code a device for an individual request. Interactive and argument
input both go through the same selection, including the adapted implementation.
The other module, two-way adaptation, is not used.

A new order type subclasses CafeOrder; a new output implements OrderOutput. New
registries are supplied through the service constructor without changing existing
order, output or service classes. Composition-root registration remains necessary
to expose an extension in an executable. A test supplies a new delivery-order
subclass and another output using new wiring while leaving production classes unchanged.

## Validation and limitation

JUnit 5 tests use recording outputs and a stub legacy printer to check delegation,
formatting, failures and conversion. Integration tests cover all six combinations.
The UML matches production classes. One limitation is that all outputs simulate
hardware in the console: no durable queue, real printer acknowledgement or retry
mechanism exists. Counters reset when the application exits.
