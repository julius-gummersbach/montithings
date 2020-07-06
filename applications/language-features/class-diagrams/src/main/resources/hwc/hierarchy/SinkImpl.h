#pragma once
#include "SinkInput.h"
#include "SinkResult.h"
#include "IComputable.h"
#include <stdexcept>

namespace montithings {
namespace hierarchy {

class SinkImpl : IComputable<SinkInput,SinkResult>{
	
private:  
    
public:
    SinkImpl()
    {
    }
	SinkResult getInitialValues() override;
	SinkResult compute(SinkInput input) override;
};

}}