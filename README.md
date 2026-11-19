# Rosewood Payroll: net pay service

Works out weekly payslips: basic pay, overtime, tax and net pay. It rejects bad input clearly
instead of paying 0. Started as AI-written code; [ANALYSIS.md](ANALYSIS.md) has what was wrong,
the plan and the decisions.

## Running it

You need JDK 21 or later, and Maven.

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.rosewood.payroll.Demo
```

`Demo` moved out of `com.rosewood.payroll`, but this command still works: `pom.xml` fixes the
main class as `Demo`, so `-Dexec.mainClass` is ignored. A plain `mvn compile exec:java` works too.

The demo runs a sample week:

```text
Payslips:
  Efua Sarpong: basic 800.00, overtime 600.00, tax 210.00, net 1190.00
  Kwame Owusu: basic 1200.00, overtime 450.00, tax 247.50, net 1402.50
Not paid:
  unknown: No employee with ID 'unknown'
  e3: Hours worked must be between 0.0 and 168.0, got -5.0
Total net pay: 2592.50 (2 paid, 2 not paid)
```

Efua has two 30-hour lines, which are added up and paid once, so she gets 20 hours of overtime.

## Testing

```bash
mvn test                                                    # 73 tests
mvn verify                                                  # tests + coverage: target/site/jacoco/index.html
mvn test-compile org.pitest:pitest-maven:mutationCoverage   # mutation testing: target/pit-reports/index.html
```

- Coverage: 100% of lines and branches, outside `Demo`.
- PIT: 42 of 42 mutants caught.
- Each test checks an exact result, so it fails if the behaviour is wrong.

PIT is 1.30.2, because 1.16.1 can't read Java 27 class files. It still works on JDK 21.

## Design

```text
src/main/java/
├── Demo.java      sets everything up and prints the report
├── domain/        Employee, HoursWorked, Payslip, PayrollCalculator
├── pay/           GrossPayCalculator: basic pay and overtime
├── tax/           TaxPolicy, TaxBand, BandedTaxPolicy
├── report/        PayrollRun, PayrollRunReport, PayrollEntry, PayrollFailure
├── repository/    EmployeeRepository, InMemoryEmployeeRepository
├── validation/    InputValidator
└── exception/     EmployeeNotFoundException
```

`PayrollCalculator.calculatePayslip(employeeId, hoursWorked)` checks the ID, looks up the
employee, then works out basic pay, overtime, tax and net pay. It receives the repository, the
gross pay calculator and the tax policy through its constructor, so tests can swap any of them.

- Overtime: hours over 40 are paid at time and a half.
- Money is `BigDecimal`. Basic pay, overtime and tax are rounded to 2 decimal places, half up,
  and net pay is gross minus tax, so every payslip adds up.
- Only `Demo` prints.

## Error handling

| Problem | Thrown by | Exception |
| --- | --- | --- |
| Unknown employee | `EmployeeRepository.getById` | `EmployeeNotFoundException` |
| Hours outside 0 to 168, or a rate of 0 or less | `HoursWorked` when created | `IllegalArgumentException` |
| Blank employee ID or name | `PayrollCalculator`, `Employee` | `IllegalArgumentException` |

The class that finds the problem throws. The caller that can decide what to do catches:
`PayrollRun` catches both exceptions for each employee, records the reason and carries on.
Nothing catches `Exception` in general, so real bugs still show.

## Changing tax rules

Tax sits behind the `TaxPolicy` interface. Today's rule, `BandedTaxPolicy`, taxes all of gross
pay at the rate of the highest band it reaches:

| Gross pay from | Rate |
| --- | --- |
| 0 | 5% |
| 1000 | 15% |
| 5000 | 25% |

- To change a limit or rate, or add a band, edit `ROSEWOOD_BANDS` in `BandedTaxPolicy`. Bands
  must start at 0 and rise in order, or the policy refuses them.
- To change how tax works, for example marginal tax where each rate applies only to the pay
  inside its band, write a new class that implements `TaxPolicy` and pass it to
  `PayrollCalculator`. Nothing else changes.

## Weekly payroll run

`PayrollRun.run(entries)` pays a week of `PayrollEntry` lines (employee ID, hours, rate) and
returns a `PayrollRunReport`.

- Each employee's lines are added up and paid once, so overtime is worked out on the week.
- A failure covers that employee's whole week. It's listed with its reason, and the run carries
  on with everyone else. Failures are an unknown employee, a bad line, lines adding up to more
  than 168 hours, or lines with different hourly rates.
- The report totals net pay over the payslips.
- Running the same week again gives the same report, and nothing is saved, so a re-run is safe.
