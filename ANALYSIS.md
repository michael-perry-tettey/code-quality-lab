# Analysis: Rosewood Payroll

Line numbers refer to `PayrollCalculator.java`.

## 1. What running it showed

- The demo pays an unknown employee GHS 0.0, with no error.
- Every result is printed twice: once by the calculator, once by the demo.
- All 3 tests pass, but PIT catches only 3 of 21 mutants (14%). The tests still pass when pay is
  divided instead of multiplied, or when tax is added instead of subtracted.

## 2. Problems, most important first

| # | Problem | Where | Breaks | Why it matters |
| --- | --- | --- | --- | --- |
| <a id="p1"></a>1 | Any failure returns 0 | `catch (Exception) { return 0; }`, lines 32–35; `findById` returns `null` | Exception handling | An error looks like a real zero payslip, and nobody finds out |
| <a id="p2"></a>2 | Tests prove nothing | `PayrollCalculatorTest` (section 3) | Testing | Bugs pass unnoticed |
| <a id="p3"></a>3 | Repository created inside the class | line 6 | Dependency inversion | Can't pass in a mock, so the failure cases can't be tested |
| <a id="p4"></a>4 | No input checks | line 8 | Fail fast | −10 hours gives a −190 payslip |
| <a id="p5"></a>5 | Money is a `double`, never rounded | line 29 | Correctness | 37.5 h at 19.99 pays 712.14375 |
| <a id="p6"></a>6 | One method does everything | lines 8–36 | Single responsibility | Lookup, overtime, tax and printing all change together |
| <a id="p7"></a>7 | Calculator prints | line 30 | No side effects | Output can't be controlled or tested |
| <a id="p8"></a>8 | Tax bands are hard-coded branches | lines 20–27 | Open-closed | Every tax change means editing this method |
| <a id="p9"></a>9 | Magic numbers | 40, 0.5, 1000, 5000, 0.05, 0.15, 0.25 | Named constants | 40 is written twice; meanings live in comments |

Minor: `Employee` could be a record; the repository holds demo data; the tests use wildcard
imports.

## 3. AI tests

| Test | Problem |
| --- | --- |
| `calculatesNetPay` | `net > 0` accepts any positive number |
| `calculatesOvertimePay` | Mocks the class under test, so the real code never runs |
| `returnsPayForAnEmployee` | `assertNotNull` on a `double` can never fail |

Not tested at all: unknown employee, overtime, the 1000 and 5000 boundaries, bad input.

## 4. Gaps and assumptions

The code and README leave these open. This is what I'll do and why.

| Gap | What I'll do | Why |
| --- | --- | --- |
| Tax cliff: gross 999.99 nets 949.99, but 1000.00 nets 850.00 | Keep one rate on all of gross pay | Changing it would change everyone's pay without being asked. Tax sits behind `TaxPolicy`, so switching to marginal tax is one new class |
| Unknown employee | Throw `EmployeeNotFoundException` with the ID; `Demo` reports it | Payroll must never quietly pay 0. The caller decides how to report it |
| Rounding | Round net pay to 2 decimal places (pesewas), half up, once at the end | Cedis have pesewas. Rounding once avoids errors adding up |
| Pay period | One call is one week; overtime is hours over 40 | 40 hours is a standard working week |
| Bad input | Reject hours below 0, above 168 or `NaN`; a rate of 0 or less; a null or blank employee ID. Zero hours is allowed and pays 0 | A week can't hold more than 168 hours, and a negative payslip is never right |
| Hourly rate | Keep it as a parameter, not on `Employee` | Keeps the existing call the same; nothing in the code says otherwise |
| Repeated calls | Keep the calculation pure: no saved state, no printing. Same input, same payslip, nothing changed | Safe to retry. If a payroll run that stores payslips is added later, key them by employee and week so a re-run can't pay twice |

## 5. Plan

### Expected behaviour

The tests check this behaviour from the spec, not what the code does today.

| Case | Expected | Code today |
| --- | --- | --- |
| 40 h at 20 | 760.00 | passes |
| 50 h at 30 (overtime) | 1402.50 | passes |
| Exactly 40 h at 10 | 380.00, no overtime | passes |
| 41 h at 10 | 394.25 | passes |
| Gross 999.99 (5%) | 949.99 | fails: 949.9905 |
| Gross 1000 (15%) | 850.00 | passes |
| Gross 5000 (25%) | 3750.00 | passes |
| Unknown employee | `EmployeeNotFoundException` | fails: returns 0 |
| Hours below 0, above 168 or `NaN` | `IllegalArgumentException` | fails: −10 h returns −190 |
| Rate of 0 or less | `IllegalArgumentException` | fails: returns 0 or less |
| Null or blank employee ID | `IllegalArgumentException` | fails: returns 0 |

### Steps, one branch each

1. `test/expected-net-pay`: replace the AI tests with the table above. The failing rows show
   the bugs.
2. `refactor/packages`: move classes into packages; `Demo` stays put so the README command works.
3. `refactor/inject-repository`: repository passed in; `findById` returns `Optional`.
4. `fix/unknown-employee`: throw `EmployeeNotFoundException`; remove the catch-all.
5. `fix/validate-input`: reject bad hours, rates and employee IDs.
6. `refactor/split-pay-and-tax`: overtime in its own class, tax behind a `TaxPolicy` interface
   with the bands as data, named constants.
7. `refactor/return-payslip`: return a `Payslip`; only `Demo` prints.
8. `fix/money-rounding`: `BigDecimal`, rounded to the pesewa.

### Planned improvement beyond the brief: weekly payroll run

After the fixes, on its own branch (`feat/payroll-run`):

- Takes a list of employees with their hours and rate, and returns one report for the week.
- Each line is a payslip broken down into basic pay, overtime, tax and net pay, so anyone can
  check how the number was reached.
- A failure (unknown employee, bad hours) is listed in the report with its reason, and the run
  carries on with everyone else. One bad entry doesn't stop the whole payroll.
- Totals at the end: total net pay and the number of payslips and failures.
- Same input gives the same report, and nothing is saved, so re-running a week is safe (see
  "Repeated calls" in section 4).

Why: it uses the new error handling the way a real payroll needs it, and the breakdown makes each
payslip easy to check.

### Layout

```text
src/main/java/
├── Demo.java
├── domain/        Employee, HoursWorked, Payslip, PayrollCalculator
├── pay/           GrossPayCalculator
├── tax/           TaxPolicy, TaxBand, BandedTaxPolicy
├── report/        PayrollRun, PayrollRunReport
├── repository/    EmployeeRepository, InMemoryEmployeeRepository
├── validation/    InputValidator
└── exception/     EmployeeNotFoundException
```

Everything sits straight under `src/main/java`. `pom.xml` fixes the main class as `Demo`, so the
old `-Dexec.mainClass=com.rosewood.payroll.Demo` command and a plain `mvn exec:java` both run it.
PIT is pointed at the new packages.

Tax gets its own package because it's the part most likely to change: a new rule is one new
`TaxPolicy` class.

## 6. Decisions

| Decision | Why | Fixes |
| --- | --- | --- |
| Unknown employee throws `EmployeeNotFoundException` (unchecked); catch-all removed | A failure must never look like a 0 payslip | [#1 Failures return 0](#p1) |
| `HoursWorked` requires hours between 0 and 168 | A week has 168 hours; negative or `NaN` hours give nonsense pay | [#4 No input checks](#p4) |
| `HoursWorked` requires a rate above 0 | A zero or negative rate can't produce a correct payslip | [#4 No input checks](#p4) |
| Employee ID must not be blank, checked before the lookup | Fails with a clear message before any lookup | [#4 No input checks](#p4) |
| Generic checks in `InputValidator` (`requireText`, `requireBetween`, `requirePositive`), each given the value's name; static, throwing `IllegalArgumentException` | Reused for any field without a new validator; the payroll limits stay in `HoursWorked`; static because there's no state to swap | [#4 No input checks](#p4) |
| Repository passed in through the constructor; `getById` throws `EmployeeNotFoundException` for an unknown ID | Tests can use a mock; the repository reports "not found" itself, so no caller can ignore it | [#3 Repository created inside the class](#p3) |
| Code is written step by step: plain `for` loops, `if` and `throw`, one call per line. Lambdas only where JUnit (`assertThrows`) or Mockito need them | Each step, and where each failure is thrown, is visible to the reader | – |
| `calculateNetPay` takes `(employeeId, HoursWorked)` instead of three loose values | Fewer arguments, and the hours and rate are already checked when they arrive | [#4 No input checks](#p4) |
| Overtime in `GrossPayCalculator`, with named constants | One job per class; numbers have names | [#6 One method does everything](#p6); [#9 Magic numbers](#p9) |
| Tax behind `TaxPolicy`, bands as data, flat rate kept | A new tax rule is one new class, which absorbs the coming change | [#8 Tax bands are hard-coded](#p8) |
| Calculator returns a `Payslip`; only `Demo` prints | No side effects; results can be tested | [#7 Calculator prints](#p7) |
| Money as `BigDecimal`, 2 decimal places, half up, rounded once | Exact amounts, no drift | [#5 Money is a `double`](#p5) |
| Kept from AI: repository interface, overtime formula, band limits and rates | Correct: 50 h at 30 gives 1650 gross × 0.85 = 1402.50, matching the demo | – |
| Rewritten from AI: error handling and all three tests | They hid failures and proved nothing | [#1 Failures return 0](#p1); [#2 Tests prove nothing](#p2) |
| Every expected-behaviour row is a test; full branch coverage; every PIT mutant killed | Shows the tests would catch a wrong result | [#2 Tests prove nothing](#p2) |
