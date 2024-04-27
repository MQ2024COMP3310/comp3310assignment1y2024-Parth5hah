import java

from Call call
where call.getCallee().getName() = "getMessage" and 
call.getCallee().getDeclaringType() instanceof TypeThrowable and
call.getNumArgument() = 0
select call
