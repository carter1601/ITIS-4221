ITIS 4221/5221 — Static Analysis with Semgrep, Fall 2026
Instructor: Chenglong Fu

START HERE
1. Read the Fall 2026 Canvas assignment. Work individually.
2. assignment/AssignmentServlet.java is the code you will analyze and patch.
3. assignment/assignment_rules_starter.yaml is unfinished. Complete its TODOs
   before scanning. An invalid-rule error is not a zero-finding result.
4. demo/ contains a different example: vulnerable code, fixed code, and a
   completed rule. Its source/sink wrappers differ from those in the assignment;
   copying the demo rules unchanged will miss the assignment's vulnerabilities.

The application source is a static exercise. Its database factory throws an
exception; no servlet runtime or database exploit is required. A real output
encoder (such as the existing Commons Text escape helper) and parameterized
SQL are still required in the patched source. Changing only a rule or declaring
an empty encoder is not a security fix.

Use local Semgrep Community Edition. No Semgrep account is required. In a
terminal opened in this package directory, after completing the rules:

  semgrep scan --oss-only --metrics=off --config assignment/assignment_rules_starter.yaml assignment/AssignmentServlet.java

Preserve an unmodified copy for your before-scan evidence, then scan your
patched Java file with the same rules. Keep both source and output in your
submission. The Canvas assignment contains the complete deliverables, rubric,
deadline, and submission instructions.
