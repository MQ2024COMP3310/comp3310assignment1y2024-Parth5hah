import java

from MethodCall call
where call.getMethod().getName() = "println" or
call.getMethod().getName() = "print"
select call